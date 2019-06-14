	#!/bin/bash

	function help_me(){
	  echo -e "Usage:-"
	  echo -e "$0 <vpc-cidr-block> <region-name> <subnet1-cidr-block> <subnet2-cidr-block> <subnet3-cidr-block>"
	  exit
	}

	CIDR_BLOCK=$1
	REGION_NAME=$2
	SUBNET1_CIDR=$3
	SUBNET2_CIDR=$4
	SUBNET3_CIDR=$5

	destinationCidrBlock="0.0.0.0/0"
	# CIDR_BLOCK="10.0.0.0/16"
	# REGION_NAME="us-east-1"
	# SUBNET1_CIDR="10.0.10.0/24"
	# SUBNET2_CIDR="10.0.50.0/24"
	# SUBNET3_CIDR="10.0.60.0/24"

	if [ $# -ne 5 ]
	then 
		echo -e "You are missing some parameters"
		help_me
	fi

	# Create subnet cidr array
	SUBNET_ARR=($SUBNET1_CIDR $SUBNET2_CIDR $SUBNET3_CIDR)

	#Check if region name exist
	REG_NAME=$(aws ec2 describe-regions --filters Name=region-name,Values="$REGION_NAME" --query Regions[].{RegName:RegionName} --output text)
	if [ -z "$REG_NAME" ]
	then
		echo -e "Region $REGION_NAME is not valid"
		exit 1
	fi

	#create vpc with cidr block
	echo "Creating VPC"
	sleep 10
	VPC_ID=$(aws ec2 create-vpc --cidr-block "$CIDR_BLOCK" --query 'Vpc.{VpcId:VpcId}' --output text)

	if [ $? -ne 0 ] 
	then 
	    echo "Failed to create VPC"
	    exit 1
	fi
	echo "Waiting for VPC to be available"

	aws ec2 wait vpc-available --vpc-ids "$VPC_ID"

	if [ $? -eq 255 ] 
	then 
	    echo "VPC still not available\n"
	    exit 1
	fi

	echo -e "VPC available"
	echo -e "VPC created with id $VPC_ID \n"

	echo -e "Assigning VPC name"
	#name the vpc
	aws ec2 create-tags --resources "$VPC_ID" --tags Key=Name,Value=VPC_"$VPC_ID"
	echo -e "Assigned name VPC_$VPC_ID \n"

	#add dns support
	echo -e "Adding DNS support"
	modify_response=$(aws ec2 modify-vpc-attribute --vpc-id "$VPC_ID" --enable-dns-support "{\"Value\":true}")
	echo -e "DNS support added \n"

	#add dns hostnames
	echo -e "Adding DNS hostnames"
	modify_response=$(aws ec2 modify-vpc-attribute --vpc-id "$VPC_ID" --enable-dns-hostnames "{\"Value\":true}")
	echo -e "Added DNS hostnames \n"

	#create subnets for vpc with cidr block
	echo -e "Creating Subnets"

	SUBNET_IDS=()
	for i in ${!SUBNET_ARR[@]}
	do
		AVAILABILITY_ZONE=$(aws ec2 describe-availability-zones --filters Name=region-name,Values="$REGION_NAME" --query AvailabilityZones[$i].{ZoneName:ZoneName} --output text)
		SUBNET_ID=$(aws ec2 create-subnet --cidr-block "${SUBNET_ARR[$i]}" --availability-zone "$AVAILABILITY_ZONE" --vpc-id "$VPC_ID" --query 'Subnet.{SubnetId:SubnetId}' --output text)
		SUBNET_IDS+=($SUBNET_ID)

		if [ $? -ne 0 ] 
		then 
		    echo "Failed to create subnet"
		    exit 1
		fi

		aws ec2 wait subnet-available --subnet-ids "$SUBNET_ID"
		if [ $? -eq 255 ] 
		then 
		    echo "Subnet still not available"
		    exit 1
		fi

		echo -e "Subnet with ID $SUBNET_ID created"


		#name the subnet
		echo -e "Assigning Subnet name"

		aws ec2 create-tags \
		  --resources "$SUBNET_ID" \
		  --tags Key=Name,Value=Subnet_"$SUBNET_ID"

		echo -e "Assigned name Subnet_$SUBNET_ID"
	done

	echo -e "Subnets created \n "


	#create internet gateway
	echo -e "Creating internet gateway"

	GATEWAY_ID=$(aws ec2 create-internet-gateway --query 'InternetGateway.{InternetGatewayId:InternetGatewayId}' --output text)

	if [ $? -ne 0 ] 
	then 
	    echo "Failed to create internet gateway"
	    exit 1
	fi

	echo -e "Internet gateway created with Id $GATEWAY_ID \n"

	#name the internet gateway
	echo -e "Assigning gateway name"
	aws ec2 create-tags --resources "$GATEWAY_ID" --tags Key=Name,Value=Internet_Gateway_"$GATEWAY_ID"
	echo -e "Assigned name Internet_Gateway_$GATEWAY_ID \n"

	#attach gateway to vpc
	echo -e "Attaching internet gateway to VPC"
	RESPONSE=$(aws ec2 attach-internet-gateway --internet-gateway-id "$GATEWAY_ID" --vpc-id "$VPC_ID")
	echo -e "Internet gateway attached to VPC \n"

	#create route table for vpc
	echo -e "Creating Route table for VPC"
	ROUTE_TABLE_ID=$(aws ec2 create-route-table --vpc-id "$VPC_ID" --query 'RouteTable.{RouteTableId:RouteTableId}' --output text)

	if [ $? -ne 0 ] 
	then 
	    echo -e "Failed to create route table"
	    exit 1
	fi
	echo -e "Route table created with Id $ROUTE_TABLE_ID \n"

	#name the route table
	echo -e "Assigning Route table name"

	aws ec2 create-tags \
	  --resources "$ROUTE_TABLE_ID" \
	  --tags Key=Name,Value=RouteTable_"$ROUTE_TABLE_ID"
	echo -e "Assigned name RouteTable_$ROUTE_TABLE_ID \n"

	#add route for the internet gateway
	echo -e "Creating Route for Internet Gateway"

	ROUTE_RESPONSE=$(aws ec2 create-route --route-table-id "$ROUTE_TABLE_ID" --destination-cidr-block "$destinationCidrBlock" --gateway-id "$GATEWAY_ID")
	if [ $? -ne 0 ] 
	then 
	    echo -e "Failed to create route for the internet gateway"
	    exit 1
	fi
	echo -e "Created route for the internet gateway \n"

	#add route to subnet
	echo -e "Associating Subnets to route table"
	for subnetid in ${SUBNET_IDS[@]}
	do
	aws ec2 associate-route-table --subnet-id "$subnetid" --route-table-id "$ROUTE_TABLE_ID"
	done
	echo "End of Script"
