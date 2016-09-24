# sample-serverless

This project is sample of [sbt-aws-serverlss](https://github.com/yoshiyoshifujii/sbt-aws-serverless) plugin.

# Useage

## create API Gateway REST API

```sh
\$ git clone https://github.com/yoshiyoshifujii/sample-serverless.git
\$ cd sample-serverless/
\$ ./bin/activator -mem 2048 \
  -DAWS_REGION=<Region Name> \
  -DAWS_ACCOUNT_ID=<AWS Account ID> \
  -DAWS_ROLE_ARN=arn:aws:iam::<AWS Account ID>:role/<Role Name> \
  -DAWS_BUCKET_NAME=<Bucket Name> \
  -DAUTH_KEY=hoge
> createApiGateway <name> [description]
ApiGateway created: <Rest APi ID>
[success] ...
```

## deploy

```sh
\$ ./bin/activator -mem 2048 \
  -DAWS_REGION=<Region Name> \
  -DAWS_ACCOUNT_ID=<AWS Account ID> \
  -DAWS_REST_API_ID=<Rest Api ID> \ # <- add this line
  -DAWS_ROLE_ARN=arn:aws:iam::<AWS Account ID>:role/<Role Name> \
  -DAWS_BUCKET_NAME=<Bucket Name> \
  -DAUTH_KEY=hoge
> deploy
...
Lambda Deploy: arn:aws:lambda:<Region Name>:<AWS Account ID>:function:SampleHello
Publish Lambda: arn:aws:lambda:<Region Name>:<AWS Account ID>:function:SampleHello:1
Create Alias: arn:aws:lambda:<Region Name>:<AWS Account ID>:function:SampleHello:test1
Create Alias: arn:aws:lambda:<Region Name>:<AWS Account ID>:function:SampleHello:production1
Api Gateway Deploy
[success] ...
```

## create API Gateway deployment and stage for test.

```sh
> createDeployment test
[success] ...
```

## test

```sh
> testMethod test
test method success.
[success] ...
```

## update stages

```sh
> getDeployments
=====================================================================================
<Rest Api ID>
=====================================================================================
| Created Date                   | Deployment Id   | Description                    |
|--------------------------------|-----------------|--------------------------------|
| Mon Sep 19 19:56:01 JST 2016   | xxxxxx          | 0.1                            |
[success] ...

> updateStages xxxxxx
[success] ...
```

# curl

```sh
\$ curl -i -X GET https://<Rest Api ID>.execute-api.<Region Name>.amazonaws.com/test/hellos -H "Authorization:hoge"
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 21
Connection: keep-alive
Date: Mon, 19 Sep 2016 11:47:36 GMT
x-amzn-RequestId: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
X-Cache: Miss from cloudfront
Via: 1.1 xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.cloudfront.net (CloudFront)
X-Amz-Cf-Id: xxxxxxx-xxxxxxxxxxxxxxxxxx_xxxxxxxx-xxxxxxxxxxxxxxxxxx==

{"message":"World!!"}%
```

