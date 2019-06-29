# Instructions on Scripts and Structure of Server Cluster
  
    Make sure you have installed AWS Cli on machine and granted User access to it


<p>"csye6225-cf-networking.json"</p>
<ul>
  <li>The cloudFormation template for network stack</li>
</ul>
<p>"csye6225-cf-application.json"</p>
<ul>
  <li>The cloudFormation template for application stack</li>
</ul>


## "csye6225-aws-cf-create-stack.sh" script will
<ul>
  <li>Create a cloudformation stack taking STACK_NAME as parameter</li>
  <li>Create and configure required networking resources</li>
  <li>Create a Virtual Private Cloud (VPC) resource called STACK_NAME-VPC</li>
  <li>Create Internet Gateway resource called STACK_NAME-InternetGateway</li>
  <li>Attach the Internet Gateway to STACK_NAME-VPC </li>
  <li>Create 3 Public Subnets with the name of STACK_NAME-PublicSubnet1 , STACK_NAME-PublicSubnet2 ,STACK_NAME-PublicSubnet3</li>
  <li>Create a public Route Table called STACK_NAME-RouteTable</li>
  <li>Create a public route in STACK_NAME-RouteTable with destination CIDR block 0.0.0.0/0 and STACK_NAME-InternetGateway as the target</li>
</ul>

<p>Open terminal and run the script in the below format with parameters</p>

   ```sh
        $ ./csye6225-aws-cf-create-stack.sh <stack-name> <region-name> <vpc-cidr-block> <subnet1-cidr-block> <subnet2-cidr-block> 
 ```

## "csye6225-aws-cf-create-application-stack.sh" script will
<ul>
  <li>Create EC2 launch configuration with User-data</li>
  <li>Create a DynamoDB to store tokens</li>
  <li>Create a RDS server to store POJO</li>
  <li>Create Instance and DB SecurityGroups </li>
</ul>

<p>Open terminal and run the script in the below format with parameters</p>

   ```sh
        $ ./csye6225-aws-cf-create-application-stack.sh <application-stack-name> <network-stack-name> <ami-id>
 ```
 
## Termination stack scripts: 
  script should take STACK_NAME as parameter
  Sequence of termination stacks should be application->cicd->network
<ul>
  <li> "csye6225-aws-cf-terminate-stack.sh": Delete the stack and all networking resources.</li>
  
     ```sh
        $ ./csye6225-aws-cf-termiante-stack.sh <stack-mame>
     ```
 
  <li> "csye6225-aws-cf-terminate-application-stack.sh": Delete the stack and all application and server resources</li>
</ul>
        


