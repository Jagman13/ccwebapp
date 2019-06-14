	#!/bin/bash

	function help_me(){
	  echo -e "Usage:-"
	  echo -e "$0 <vpc-id>"
	  exit
	}

	VPC_ID=$1

	if [ $# -ne 1 ]
	then 
		echo -e "You are missing some parameters"
		help_me
	fi

	#Find all subnet ids for a VPC Id
	SUBNET_ARR=$(aws ec2 describe-subnets --filters Name=vpc-id,Values="$VPC_ID" --query Subnets[*].{SubnetId:SubnetId} --output text)

	ROUTES_TABLES_ARR=$(aws ec2 describe-route-tables --filters "Name=vpc-id,Values=$VPC_ID" \
	 --query 'RouteTables[?Associations[0].Main!=`true`].{ID:RouteTableId}' \
	 --output text)

	for subnetid in ${SUBNET_ARR[@]}
	do
	    echo -e "Deleting subnet with id $subnetid"
	    #Delete subnet
	    aws ec2 delete-subnet --subnet-id "$subnetid"
	     if [ $? -ne 0 ] 
		then 
		    echo -e "Failed to delete Subnet with id $subnetid"
		    exit 1
	fi
	    echo -e "Deleted subnet with id $subnetid successfully \n"
	done

	#Find custom route table ids for a VPC id
	for route_table_id in ${ROUTES_TABLES_ARR[@]}
	do
	    echo -e "Deleting routetable with id $route_table_id"
	    #Delete custom route table
	    aws ec2 delete-route-table --route-table-id "$route_table_id"
	     if [ $? -ne 0 ] 
		then 
		    echo -e "Failed to delete Custom route table"
		    exit 1
	fi
	    echo -e "Deleted routetable with id $route_table_id successfully \n"
	done

	#Find all internet-gateway ids for a VPC id
	INTERNET_GATEWAY_ARR=$(aws ec2 describe-internet-gateways --filters Name=attachment.vpc-id,Values="$VPC_ID" \
	 --query 'InternetGateways[*].{InternetGatewayId:InternetGatewayId}' \
	 --output text)

	 for internet_gateway_id in ${INTERNET_GATEWAY_ARR[@]}
	 do 
	    #Detach Internet gateway from VPC
	    echo -e "Detach internet gateway fom VPC"
	    aws ec2 detach-internet-gateway --internet-gateway-id "$internet_gateway_id" --vpc-id "$VPC_ID"
	     if [ $? -ne 0 ] 
		then 
		    echo -e "Failed to detach Internet gateway from VPC"
		    exit 1
	fi
	    echo -e "Detached internet gateway fom VPC successfully \n"

	    #Delete internet gateway
	 	echo -e "Deleting internet gateway with id $internet_gateway_id"
	    aws ec2 delete-internet-gateway --internet-gateway-id "$internet_gateway_id"
	     if [ $? -ne 0 ] 
		then 
		    echo -e "Failed to delete internet gateway"
		    exit 1
	fi
	    echo -e "Deleted internet gateway with id $internet_gateway_id successfully \n"
	 done

	 #Delete VPC
	 echo -e "Deleting VPC with Id $VPC_ID"
	 aws ec2 delete-vpc --vpc-id "$VPC_ID"
	 if [ $? -ne 0 ] 
		then 
		    echo -e "Failed to delete VPC"
		    exit 1
	fi
	  echo -e "Deleted VPC with Id $VPC_ID successfully \n"
	  echo "End of Script"
