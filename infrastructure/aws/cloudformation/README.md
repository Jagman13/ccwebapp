# CSYE 6225 - Summer 2019

# Creating/Deleting resources using Cloud Formation!

  - Script csye6225-aws-cf-create-stack.sh can be used to create resources (Stack ,VPC, Subnets, Internet gateway, route tables etc.)
  - Script csye6225-aws-cf-termiante-stack.sh can be used to delete the resources created

# How to run the scripts
  - Instructions are valid for running script using Linux terminal
  - Open terminal and run the script in the below format with parameters
    - Usage for csye6225-aws-cf-create-stack.sh
        ```sh
        $ ./csye6225-aws-cf-create-stack.sh <stack-name> <region-name> <vpc-cidr-block> <subnet1-cidr-block> <subnet2-cidr-block> <subnet3-cidr-block>
        ```
    - Usage for csye6225-aws-cf-termiante-stack.sh
        ```sh
        $ ./csye6225-aws-cf-termiante-stack.sh <stack-mame>
        ```


