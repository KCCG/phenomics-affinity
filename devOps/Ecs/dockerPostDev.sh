#!/bin/bash

echo 'Tagging docker image'
docker tag phenomics-affinity-dev:1.0 254144944163.dkr.ecr.ap-southeast-2.amazonaws.com/phenomics-dev:affinity-latest
echo 'Tagging completed for docker image'


echo 'Getting ECR credentials'
eval $(aws ecr get-login --no-include-email | sed 's|https://||')
echo 'Received credentials, activated for 12 hours'

echo 'Uploading pipeline image to ecr'
docker push 254144944163.dkr.ecr.ap-southeast-2.amazonaws.com/phenomics-dev:affinity-latest
echo 'Image uploaded'
echo 'Affinity is ready to be deployed'

