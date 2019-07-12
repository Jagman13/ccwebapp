#!/bin/sh
#shell script to create AWS application infrastructures

 help_me()
{
    echo "Usage:-"
    echo "$0 <stack name> "
    exit
}
STACK_NAME=$1-AppStack

if [ -z "$1" ]
  then
    echo "No STACK_NAME argument supplied"
    help_me
fi

echo "Deleting Application stack..."

aws cloudformation delete-stack --stack-name $STACK_NAME

if [ $? -eq 0 ]; then
  echo "Delete in progress"
  aws cloudformation wait stack-delete-complete --stack-name $STACK_NAME
  echo "Stack deleted successfully"
else
  echo "Failure while deleting stack"
fi