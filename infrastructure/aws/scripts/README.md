# CSYE 6225 - Summer 2019

# Creating/Deleting resources using AWS CLI!

  - Script csye6225-aws-networking-setup.sh can be used to create resources (VPC, Subnets, Internet gateway, route tables etc.)
  - Script csye6225-aws-networking-teardown.sh can be used to delete the resources created

# How to run the scripts
  - Instructions are valid for running script using Linux terminal
  - Open terminal and run the script in the below format with parameters
    - Usage for csye6225-aws-networking-setup.sh
        ```sh
        $ ./csye6225-aws-networking-setup.sh <vpc-cidr-block> <region-name> <subnet1-cidr-block> <subnet2-cidr-block> <subnet3-cidr-block>
        ```
    - Usage for csye6225-aws-networking-teardown.sh
        ```sh
        $ ./csye6225-aws-networking-setup.sh <vpc-id>
        ```


