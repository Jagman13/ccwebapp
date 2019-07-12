#!/bin/sh
#shell script to create IAM stack
 
 help_me()
{
	  echo "Usage:-"
	  echo "$0 <IAM-Stack-Name> <user>"
	  exit
}

	IAM_STACK_NAME=$1-Stack
	USER=$2


	if [ $# -ne 2 ]
	then 
		echo -e "You are missing some parameters"
		help_me
fi

echo "Creating stack..."
aws cloudformation create-stack --stack-name $IAM_STACK_NAME --template-body file://csye6225-cf-iam.json --capabilities CAPABILITY_NAMED_IAM \
--parameters ParameterKey=CFNUser,ParameterValue=$USER 

if [ $? -eq 0 ]; then
echo "Creating progress..."
 aws cloudformation wait stack-create-complete --stack-name $IAM_STACK_NAME
 	if [ $? -ne 255 ]; then
 		
  		echo "Stack created successfully!!"
  	else
  		echo "Failure while waiting for stack-create-complete !!"
  	fi

else
  echo "Failure while creating stack !!"

fi



