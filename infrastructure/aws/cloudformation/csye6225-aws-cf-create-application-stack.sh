#!/bin/sh
#shell script to create AWS network infrastructures

 
 help_me()
{
	  echo "Usage:-"
	  echo "$0 <Application-Stack-Name> <Network-Stack-Name> <AMI-id> <Keyname> <S3CodeDeployBucket> <S3ImageBucket> <User> <snstopicname>"
	  exit
}

	APP_STACK_NAME=$1-AppStack
	NET_STACK_NAME=$2-NetworkStack
	AMI_ID=$3
	KeyName=$4
	CODEDEPLOYBUCKET=$5
	S3IMAGEBUCKET=$6
	USER=$7
	SNSTOPICNAME=$8

	if [ $# -ne 8 ]
	then 
		echo -e "You are missing some parameters"
		help_me
fi

echo "Going to the get the STACK id for the Network Stack : " $NET_STACK_NAME

NET_STACK_ID=$(aws cloudformation describe-stacks --stack-name $NET_STACK_NAME --query Stacks[0].StackId --output text)

if [ -z "$NET_STACK_ID" ]; then
   echo "Network Stack Doesnt not exist. Kindly provide correct name"
   exit

fi

echo " VPC id for the Network Stack is : " $NET_STACK_ID

#vpc_name="${NET_STACK_NAME}-VPC"

NET_VPC_ID=$(aws ec2 describe-vpcs --filters "Name=tag:aws:cloudformation:stack-id,Values=$NET_STACK_ID" --query 'Vpcs[0].VpcId' --output text)

echo " VPC id for the Network VPC ID is : " $NET_VPC_ID

SUBNET1_NAME=$NET_STACK_NAME-PublicSubnet-1
SUBNET2_NAME=$NET_STACK_NAME-PublicSubnet-2
SUBNET3_NAME=$NET_STACK_NAME-PublicSubnet-3

Subnet1=$(aws ec2 describe-subnets --filters "Name=tag:Name,Values=$SUBNET1_NAME" --query 'Subnets[0].SubnetId' --output text)
Subnet2=$(aws ec2 describe-subnets --filters "Name=tag:Name,Values=$SUBNET2_NAME" --query 'Subnets[0].SubnetId' --output text)
Subnet3=$(aws ec2 describe-subnets --filters "Name=tag:Name,Values=$SUBNET3_NAME" --query 'Subnets[0].SubnetId' --output text)

echo $APP_STACK_NAME
echo "Creating stack..."
aws cloudformation create-stack --stack-name $APP_STACK_NAME --template-body file://csye6225-cf-application.json \
--parameters ParameterKey=AMIID,ParameterValue=$AMI_ID \
 ParameterKey=Subnet1,ParameterValue=$Subnet1\
 ParameterKey=Subnet2,ParameterValue=$Subnet2 \
 ParameterKey=Subnet3,ParameterValue=$Subnet3\
 ParameterKey=VPC,ParameterValue=$NET_VPC_ID\
 ParameterKey=keyname,ParameterValue=$KeyName\
 ParameterKey=StackName,ParameterValue=$APP_STACK_NAME\
 ParameterKey=S3CodeDeployBucket,ParameterValue=$CODEDEPLOYBUCKET\
 ParameterKey=S3ImageBucket,ParameterValue=$S3IMAGEBUCKET\
 ParameterKey=CFNUser,ParameterValue=$USER\
 ParameterKey=SNSName,ParameterValue=$SNSTOPICNAME\
 --capabilities CAPABILITY_NAMED_IAM


 
 

if [ $? -eq 0 ]; then
echo "Creating progress..."
 aws cloudformation wait stack-create-complete --stack-name $APP_STACK_NAME
 	if [ $? -ne 255 ]; then
 		
  		echo "Stack created successfully!!"
  	else
  		echo "Failure while waiting for stack-create-complete !!"
  	fi

else
  echo "Failure while creating stack !!"

fi



