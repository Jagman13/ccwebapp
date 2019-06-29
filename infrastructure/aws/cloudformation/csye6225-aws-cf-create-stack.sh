#!/bin/sh
#shell script to create AWS network infrastructures

 
 help_me()
{
	  echo "Usage:-"
	  echo "$0 <stack name> <region-name> <vpc-cidr-block> <subnet1-cidr-block> <subnet2-cidr-block> <subnet3-cidr-block>"
	  exit
}

	STACK_NAME=$1-NetworkStack
	REGION_NAME=$2
	CIDR_BLOCK=$3
	SUBNET1_CIDR=$4
	SUBNET2_CIDR=$5
	SUBNET3_CIDR=$6

	destinationCidrBlock="0.0.0.0/0"
	# CIDR_BLOCK="10.0.0.0/16"
	# REGION_NAME="us-east-1"
	# SUBNET1_CIDR="10.0.10.0/24"
	# SUBNET2_CIDR="10.0.50.0/24"
	# SUBNET3_CIDR="10.0.60.0/24"

	if [ $# -ne 6 ]
	then 
		echo -e "You are missing some parameters"
		help_me
fi



echo "Creating stack..."
aws cloudformation create-stack --stack-name $STACK_NAME --template-body file://csye6225-cf-networking.json \
--parameters ParameterKey=EnvironmentName,ParameterValue=$STACK_NAME \
 ParameterKey=AvailabilityZone,ParameterValue=$REGION_NAME \
 ParameterKey=VPCCidrBlock,ParameterValue=$CIDR_BLOCK \
 ParameterKey=VPCSubnetCidrBlock1,ParameterValue=$SUBNET1_CIDR \
 ParameterKey=VPCSubnetCidrBlock2,ParameterValue=$SUBNET2_CIDR \
 ParameterKey=VPCSubnetCidrBlock3,ParameterValue=$SUBNET3_CIDR
 

if [ $? -eq 0 ]; then
echo "Creating progress..."
 aws cloudformation wait stack-create-complete --stack-name $STACK_NAME
 	if [ $? -ne 255 ]; then
 		
  		echo "Stack created successfully!!"
  	else
  		echo "Failure while waiting for stack-create-complete !!"
  	fi

else
  echo "Failure while creating stack !!"

fi