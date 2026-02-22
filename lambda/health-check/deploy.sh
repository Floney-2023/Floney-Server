#!/bin/bash
set -e

# ── 설정 ──────────────────────────────────────────
PROFILE="floney"
REGION="ap-northeast-2"
FUNCTION_NAME="floney-health-check"
ROLE_NAME="floney-health-check-role"
RULE_NAME="floney-health-check-schedule"

if [ -z "$SLACK_WEBHOOK_URL" ]; then
  echo "❌ SLACK_WEBHOOK_URL 환경변수를 설정해주세요."
  echo "   export SLACK_WEBHOOK_URL=https://hooks.slack.com/services/xxx"
  exit 1
fi

ACCOUNT_ID=$(aws sts get-caller-identity --profile $PROFILE --query Account --output text)
echo "✅ AWS Account: $ACCOUNT_ID"

# ── IAM Role 생성 ──────────────────────────────────
echo "▶ IAM Role 생성 중..."
TRUST_POLICY='{
  "Version": "2012-10-17",
  "Statement": [{
    "Effect": "Allow",
    "Principal": { "Service": "lambda.amazonaws.com" },
    "Action": "sts:AssumeRole"
  }]
}'

ROLE_ARN=$(aws iam create-role \
  --profile $PROFILE \
  --role-name $ROLE_NAME \
  --assume-role-policy-document "$TRUST_POLICY" \
  --query Role.Arn --output text 2>/dev/null || \
  aws iam get-role --profile $PROFILE --role-name $ROLE_NAME --query Role.Arn --output text)

aws iam attach-role-policy \
  --profile $PROFILE \
  --role-name $ROLE_NAME \
  --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole 2>/dev/null || true

echo "✅ Role ARN: $ROLE_ARN"
sleep 5  # IAM 전파 대기

# ── Lambda 패키징 ──────────────────────────────────
echo "▶ Lambda 패키징 중..."
zip -q function.zip index.mjs

# ── Lambda 생성 or 업데이트 ────────────────────────
if aws lambda get-function --profile $PROFILE --region $REGION --function-name $FUNCTION_NAME &>/dev/null; then
  echo "▶ Lambda 코드 업데이트 중..."
  aws lambda update-function-code \
    --profile $PROFILE \
    --region $REGION \
    --function-name $FUNCTION_NAME \
    --zip-file fileb://function.zip \
    --query FunctionArn --output text

  aws lambda update-function-configuration \
    --profile $PROFILE \
    --region $REGION \
    --function-name $FUNCTION_NAME \
    --environment "Variables={SLACK_WEBHOOK_URL=$SLACK_WEBHOOK_URL}" \
    --query FunctionArn --output text
else
  echo "▶ Lambda 생성 중..."
  LAMBDA_ARN=$(aws lambda create-function \
    --profile $PROFILE \
    --region $REGION \
    --function-name $FUNCTION_NAME \
    --runtime nodejs22.x \
    --role $ROLE_ARN \
    --handler index.handler \
    --zip-file fileb://function.zip \
    --timeout 30 \
    --memory-size 128 \
    --environment "Variables={SLACK_WEBHOOK_URL=$SLACK_WEBHOOK_URL}" \
    --query FunctionArn --output text)
  echo "✅ Lambda ARN: $LAMBDA_ARN"
fi

LAMBDA_ARN="arn:aws:lambda:$REGION:$ACCOUNT_ID:function:$FUNCTION_NAME"

# ── EventBridge 스케줄 (5분마다) ───────────────────
echo "▶ EventBridge 스케줄 등록 중... (5분마다)"
RULE_ARN=$(aws events put-rule \
  --profile $PROFILE \
  --region $REGION \
  --name $RULE_NAME \
  --schedule-expression "rate(5 minutes)" \
  --state ENABLED \
  --query RuleArn --output text)

aws lambda add-permission \
  --profile $PROFILE \
  --region $REGION \
  --function-name $FUNCTION_NAME \
  --statement-id "EventBridgeInvoke" \
  --action lambda:InvokeFunction \
  --principal events.amazonaws.com \
  --source-arn $RULE_ARN 2>/dev/null || true

aws events put-targets \
  --profile $PROFILE \
  --region $REGION \
  --rule $RULE_NAME \
  --targets "Id=1,Arn=$LAMBDA_ARN"

# ── 정리 ──────────────────────────────────────────
rm -f function.zip

echo ""
echo "🎉 배포 완료!"
echo "   Lambda:     $FUNCTION_NAME"
echo "   Schedule:   5분마다 실행"
echo "   비용:       프리티어 범위 내 (월 8,640회 → 무료)"
