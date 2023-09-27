# Eurosys 24 - Desiccant - AE

## 1.  Artifact Checklist

- **Hardware**: Intel X64 server

- **Operating System**: Ubuntu 20.04 (Other Linux distributions might also be supported, but they have only been tested on Ubuntu 20.04)

- **Code Archive**: https://github.com/SJTU-IPADS/Desiccant-artifacts

## 2. File Structure

The downloaded code archive contains the following files:

```shell
.
├── application
│   ├── java
│   └── nodejs
├── exp
│   ├── fig12
│   ├── fig7
│   ├── fig8
│   └── fig9,10
├── jdk8u-jdk8u322-ga
├── node-14.20
├── openwhisk-cli
├── openwhisk-client-go
├── openwhisk-docker-compose
├── openwhisk-runtime-java
├── openwhisk-runtime-nodejs
├── openwhisk-desiccant
├── openwhisk-vanilla
├── pmap-server-c
├── lambda
└── scripts
    ├── build-apps.sh
    ├── build-java-runtime.sh
    ├── build-nodejs-runtime.sh
    ├── build-openwhisk-cli.sh
    ├── build-openwhisk.sh
    └── build-pmap-server.sh
```

- application: The FaaS applications (using the programming model of OpenWhisk) tested in the paper.
  - java: FaaS applications written in the Java language.
  - nodejs: FaaS applications written in the JavaScript language and dependent on Node.js for execution.
- exp: Automated scripts used to generate each figure in the paper, which correspond to the main claims of the artifact evaluation.
- jdk8u-jdk8u322-ga: Openjdk8u322-ga with Desiccant's enhancement.
- node-14.20: Node-14.20 with Desiccants's enhancement.
- openwhisk-client-go & openwhisk-cli: files required to build a modified version (enhanced with additional timing output required in automated scripts) of the command line client of OpenWhisk (wsk cli).
- openwhisk-docker-compose: files used to start the openwhisk framework with docker compose.
- openwhisk-runtime-java/nodejs: Files required to build a Docker images used for function execution in OpenWhisk.
- openwhisk-desiccant: Source code of the OpenWhisk framework enhanced with Desiccant's policy.
- openwhisk-vanilla:  Source code of the vanilla OpenWhisk framework with some modifications to simulate AWS Lambda's configuration.
- pmap-server-c: A small http server used to perform priviledged pmap operation for the OpenWhisk framework
- lambda:  Files required to run functions on AWS Lambda (only includes Java functions, as Node.js functions produce similar results and are therefore omitted). Refer to the "Test on AWS Lambda" section for more details.
- scripts: Scripts used to build each component.

## 3. Setup

The subsequent experiments require completion of the following setup steps in advance:

### 3.1 Install Dependencies

First, follow the [official instruction guide](https://docs.docker.com/engine/install/ubuntu/) to install docker on ubuntu, and add current user to the ```docker``` user group using the following command.

```shell
sudo usermod -aG docker $USER
```

Second, install the following dependencies. Note that all the dependencies have been tested on Ubuntu 20.04 server. Thus, there might be some missing dependency issues in other environments, which can be easily fixed.

```shell
sudo apt-get install docker-compose

sudo apt-get install build-essential maven golang-go openjdk-8-jdk npm zip
sudo apt-get install libx11-dev libxext-dev libxrender-dev libxtst-dev libxt-dev
sudo apt-get install libcups2-dev libfreetype6-dev libasound2-dev libfontconfig1-dev
sudo apt install net-tools
```

Lastly, modify the necessary configurations, which include changing the default Java version to Java 8 and setting the default shell to bash.

```shell
# Change the default provider of Java to JDK 8, as it is required to build the modified OpenJDK.
sudo update-alternatives --config java
# Change the default provider of the sh command to bash, as it is required to start OpenWhisk with Docker Compose.
sudo dpkg-reconfigure dash
```

### 3.2 Fix Files

Use the following command in the ```scripts``` directory to fix some split files by merging them together:


```shell
./fix-files.sh

```

### 3.3 Build OpenWhisk

The OpenWhisk framework mainly consists of three parts: the framework container images (openwhisk), the command line client (openwhisk-cli), and the external pmap server (pmap-server). Use the following commands to build them in the ```scripts``` directory:

```shell
./build-openwhisk.sh
./build-openwhisk-cli.sh
./build-pmap-server.sh
```

### 3.4 Build Function Executor

The OpenWhisk framework requires a specific Docker image (Function Executor) for each language, which includes the language runtime and a small HTTP server for communication with the OpenWhisk framework.Use the following commands to build the function executor for Java and JavaScript in the ```scripts``` folder:

```shell
./build-java-runtime.sh
./build-nodejs-runtime.sh
```

### 3.5 Build Applications

All FaaS applications for OpenWhisk are located in the ```application``` directory. To build them, use the following command in the ```script``` directory. The ```<IP_ADDR>``` the externally accessible IP address of the current machine (e.g., 192.168.1.101). The script will replace the ip address of databases in the applications with the provided IP_ADDR subsequently access the databases in containers.

```shell
./build-apps.sh <IP_ADDR>
```

### 3.6 Reconfigure CPU Binding

The archive is tested on a 40-core machine, and the cores are divided into two sets to prevent interference.

- cpu-set-1 (0-19): CPUs used to run the OpenWhisk framework and the necessary experiment scripts, which are present in the following files:
  
  ```shell
  pamp-server-c/run.sh
  openwhisk-docker-compose/docker-compose-desiccant.yml
  openwhisk-docker-compose/docker-compose-vanilla.yml
  exp/fig9,10/inner/run-azure-test.py (line9)
  ```

- cpu-set-2 (20-39): CPUs used to execute FaaS functions, which are present in the following files:
  
  ```shell
  openwhisk-vanilla/core/invoker/src/main/resources/application.conf
  openwhisk-desiccant/core/invoker/src/main/resources/application.conf
  exp/fig9,10/inner/run-azure-test.py (line10)
  ```

To run the experiments in another hardware environment, please manually change the CPU binding to a different configuration.

## 4. Test on OpenWhisk

### 4.1 Normal Execution

To execute a FaaS application with desiccant, first start the OpenWhisk framework  by using the following command in the ```openwhisk-docker-compose``` directory:

```shell
sh run.sh desiccant
```

It might take some time for the first execution, but subsequent executions should be faster.

Next, create a simple tool function used to trigger Desiccant's reclaim operation on all functions with the following command in the ```application``` directory:

```shell
sh create-reclaim.sh
```

Later, navigate to each application's directory to execute it or reclaim memory using the created tool function. Every application has three major scripts in its ```scripts``` directory related to executing the application:

- prepare.sh: script used to setup the necessary environment for the application, such as storing data into the database.

- update.sh: script used to upload the application to the FaaS framework for later execution.

- invoke.sh: script used to invoke the uploaded application.

Take the ```file-hash``` function as an example, all of its scripts locates in the ```application/java/file-hash/scripts``` directory.

To prepare the necessary data in the database, use the following command:

```shell
sh ./prepare.sh
```

To upload the function to the openwhisk framework, use the following command:

```shell
sh update.sh -nogc 256
```

Later, use the following command to execute the function:

```shell
sh ./invoke.sh
```

To manually trigger memory reclamation with Desiccant's support, use the following command:

```shell
wsk action invoke ZZMReclaimAll -i --result; sleep 2
```

You can use the ```pmap``` command to check the memory consumption of the main process in the Docker container and observe the changes in memory consumption before/after the reclaim operation. For more complex experiments, please refer to Section 4.2.

### 4.2 Run Experiments

For each experiment corresponding to the main claims, the artifact contains automated scripts that can directly reproduce the data similar to that presented in the paper. Each experiment has two major scripts:

- run.sh: script used to run the experiment

- parse.sh: script used to parse the result of the experiment

Take the data in Figure 7 as an example. 

To run the experiment, navigate to the ```exp/fig7``` directory and run the following command:

```shell
./run.sh
```

After executing the experiment, run the following script to parse the collected data:

```shell
./parse.sh
```

The script will output the parsed result to stdout in CSV format, and the data should be similar to that shown in the corresponding figure (Figure 7 in this case).

## 5. Test on AWS Lambda

### 5.1 File Structure

```
├── lambda-functions
│   ├── desiccant-image
│   └── java
└── proxy-go
```

- lambda-functions: 

- - desiccant-image: Files used to create the ECR docker image required to execute functions in Lambda.
  
  - java: Source code of all Java applications used in the Lambda experiment.

- proxy-go: Source code of the proxy written in the Go language, used to trigger reclaims in the Lambda environment when required.

### 5.2 Setup

#### 5.2.1 Setup AWS ECR, VPC and Security Group

**AWS ECR** 

> AWS ECR stands for Amazon Elastic Container Registry. It is a fully managed container registry service provided by Amazon Web Services (AWS).
> 
> For more information on ECR, please refer to the following reference: https://docs.aws.amazon.com/AmazonECR/latest/userguide/get-set-up-for-amazon-ecr.html
> 
> To access the your ECR repo, please use the following link: https://us-east-1.console.aws.amazon.com/ecr/repositories?region=us-east-1

An AWS ECR is required to serve as a repository that stores the Docker images used to run functions. 

**AWS VPC & Security group**

> Virtual private clouds (VPC): A VPC is a virtual network that closely resembles a traditional network that you'd operate in your own data center.
> 
> A security group acts as a firewall that controls the traffic allowed to and from the resources in your virtual private cloud (VPC).
> 
> For more information, please refer to:
> 
> https://docs.aws.amazon.com/vpc/latest/userguide/what-is-amazon-vpc.html
> 
> https://docs.aws.amazon.com/vpc/latest/userguide/security-groups.html

An AWS VPC is requried to ensure Lambda functions can access the databases in the EC2 instances. Additionally, an AWS Security Group with all ports accessible from any IP address is required to enable access from Lambda functions to the EC2 instances.

#### 5.2.2 Launch and Setup an EC2 Instance

**Create an EC2 instance**

> AWS EC2 (Elastic Compute Cloud) is a web service provided by Amazon Web Services (AWS) that allows users to rent virtual servers in the cloud.
> 
> For more information on EC2, please refer to the following reference: https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/EC2_GetStarted.html
> 
> To access the EC2 instances belonging to your account, please use the following link: https://us-east-1.console.aws.amazon.com/ec2/home?region=us-east-1#Instances:

To ease the deployment and experiment in AWS Lambda, and to host necessary components (e.g., databases), an AWS EC2 instance with at least the following configuration is required:

```shell
AMI ID: ami-0b93ce03dcbcb10f6 

Instance type: m4.2xlarge 

Volume size (GiB): 64GiB
```

**Prepare necessary files**

Firstly, perform the following file copy operations:

- Copy all file in the ```lambda``` directory to the $HOME directory of your EC2 instance.
- Copy ```jdk8u-jdk8u322-ga/build/linux-x86_64-normal-server-release/images/j2sdk-image``` to ```$HOME/j2sdk-image``` of your EC2 instance.
- Copy ```jdk8u-jdk8u322-ga/build/linux-x86_64-normal-server-release/images/j2re-image``` to ```$HOME/lambda-functions/desiccant-image/java/aws-lambda-base-images/j2re-image``` of your EC2 instance.

Then, save your ECR Repo URL (e.g., `8*********054.dkr.ecr.us-east-1.amazonaws.com`) to the file `~/lambda-functions/lambda.ecr`, and save your VPC Config which is set when launching an EC2 instance (e.g., `SubnetIds=subnet-24******,SecurityGroupIds=sg-024*********`) to file `~/lambda-functions/lambda.vpc`.
![SubnetIds.png](https://s2.loli.net/2023/09/26/b8JaA6OIvjKiETf.png)
![SecurityGroupIds.png](https://s2.loli.net/2023/09/26/1s8YA2tcVC6Wyph.png)

**Install and  configure AWS CLI**

AWS CLI is requried to invoke Lambda functions from the command line.

Firstly, use the following commands to install it.

```shell
sudo apt install unzip
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install
aws --version
```

Later, execute `aws configure` and enter the corresponding values, the format is as follows:

```shell
ubuntu@ip-172-31-**-**:~$ aws configure
AWS Access Key ID [None]: ****************L2E2
AWS Secret Access Key [None]: ****************cAWU
Default region name [None]: us-east-1
Default output format [None]: json
```

Access Key ID & Secret Access Key can be achieved from AWS IAM, please access the following links for more details.

> https://docs.aws.amazon.com/cli/latest/userguide/cli-authentication-user.html
> 
> https://aws.amazon.com/cn/blogs/security/wheres-my-secret-access-key/

### 5.3 Execution and Evaluation

#### 5.3.1 Setup

**Install dependencies**

Use the following commands to install the necessary dependencies in the EC2 instance.

```shell
sudo apt-get update
sudo apt-get upgrade
sudo apt-get install openjdk-8-jdk maven golang-go
sudo snap install docker
```

**Create Lambda Role**

An AWS IAM Role with the necessary privileges is required to create the Lambda functions. Use the following commands to create it:

```shell
aws iam create-role --role-name lambda-ex --assume-role-policy-document '{"Version": "2012-10-17","Statement": [{ "Effect": "Allow", "Principal": {"Service": "lambda.amazonaws.com"}, "Action": "sts:AssumeRole"}]}'
aws iam attach-role-policy --role-name lambda-ex --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole
```

If success, you will see the output like this:

```json
{
    "Role": {
        "Path": "/",
        "RoleName": "lambda-ex",
        "RoleId": "*************AYX",
        "Arn": "arn:aws:iam::*********54:role/lambda-ex",
        "CreateDate": "2023-09-23T01:18:44+00:00",
        "AssumeRolePolicyDocument": {
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Effect": "Allow",
                    "Principal": {
                        "Service": "lambda.amazonaws.com"
                    },
                    "Action": "sts:AssumeRole"
                }
            ]
        }
    }
}
```

Save the arn content (e.g., ```arn:aws:iam::*********54:role/lambda-ex```) to the file ```~/lambda-functions/lambda.role```

**Build and prepare necessary components**

**Proxy-go**

Proxy-go is a proxy written in the Go language used to trigger reclaim operations in the Lambda environment when necessary. Use the following commands to build it:

```shell
sudo apt-get install golang-go
cd ~/proxy-go && sh build.sh
```

**Applications**

A set of applications are going to be executed in AWS Lambda. Use the following commands to build them and prepare the necessary environments (e.g., databases).

```shell
cd ~/lambda-functions
sh build.sh
sh prepare.sh
```

**ECR Images**

An ECR image is required to launch Lambda functions executing on language runtimes with Desiccant's enhancement. Use the following commands to create and upload it to the ECR repository:

```shell
cd /home/ubuntu/lambda-functions/desiccant-image && sh build-ecr.sh
cd /home/ubuntu/lambda-functions/desiccant-image/java/aws-lambda-base-images && sh build-all.sh
```

**Create Lambda Functions**

Use the following commands to create experimental functions in AWS Lambda:

```shell
cd ~/lambda-functions && sh create-lambda.sh
```

#### 5.3.2 Normal Execution

All lambda functions are located in the `$HOME/lambda-functions/java` directory. Each function has the following major scripts in its scripts directory:

- prepare.sh: This script is used to set up the necessary environment for the application, such as storing data into the database.

- update.sh:  This script is used to update the Lambda function's description to invalidate the previous instance cache created by Lambda.

- invoke.sh:  This script is used to invoke the Lambda function.

- update-reclaim.sh and invoke-reclaim.sh: These scripts are used to run the Desiccant version of the function.

- reclaim.sh: This script is used to trigger the reclaim operation in the Lambda environment on the Desiccant's version of the function..

To prepare the necessary data in the database, use the following command:

```shell
sh ./prepare.sh
```

Later, use the following command to execute the function:

```shell
sh ./update.sh
sh ./invoke.sh
```

If you want to execute the Desiccant version of the function, use the following commands:

```shell
sh ./update-reclaim.sh 
sh ./invoke-reclaim.sh
sh ./reclaim.sh
```

#### 5.3.3 Run Experiment

We provide automated scripts to reproduce the data in AWS Lambda (Figure 11 in the paper). To execute the experiment, use the following commands in the `$HOME/lambda-functions/` directory.

```shell
./run.sh
```

After executing the experiment, run the following script to parse the collected data:

```shell
./parse.sh
```

Node.js functions have similar results to Java functions and are therefore omitted in this artifact.
