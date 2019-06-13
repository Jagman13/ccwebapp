#!/bin/sh
#shell script to create AWS network infrastructures

 echo "Enter Stack Name: "
 read STACK_NAME
 echo "Enter Region:"
 read  region
 echo "Enter VPCCidrBlock:"
 read  vpcCidrBlock
 echo "Enter VPCSubnetCidrBlock1:"
 read  subnetCidrBlock1
 echo "Enter VPCSubnetCidrBlock2:"
 read  subnetCidrBlock2
 echo "Enter VPCSubnetCidrBlock3:"
 read  subnetCidrBlock3


if [ -z "$STACK_NAME" ] || [ -z "$region" ] || [ -z "$vpcCidrBlock" ] || [ -z "$subnetCidrBlock1" ] || [ -z "$subnetCidrBlock2" ] || [ -z "$subnetCidrBlock3" ] ; then
	echo "Please provide all the values"
	exit 1
fi




echo "Creating stack..."
aws cloudformation create-stack --stack-name $STACK_NAME --template-body file://csye6225-cf-networking.json \
--parameters ParameterKey=EnvironmentName,ParameterValue=$STACK_NAME \
 ParameterKey=AvailabilityZone,ParameterValue=$region \
 ParameterKey=VPCCidrBlock,ParameterValue=$vpcCidrBlock \
 ParameterKey=VPCSubnetCidrBlock1,ParameterValue=$subnetCidrBlock1 \
 ParameterKey=VPCSubnetCidrBlock2,ParameterValue=$subnetCidrBlock2 \
 ParameterKey=VPCSubnetCidrBlock3,ParameterValue=$subnetCidrBlock3
 

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