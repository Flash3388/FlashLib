#!/usr/bin/env bash
if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
  openssl aes-256-cbc -K 
  ./gradlew publish -PNEXUS_USERNAME=${NEXSUS_USERNAME} -PNEXUS_PASSWORD=${NEXUS_PASSWORD} -Psigning.keyId=${GPG_KEY_ID} -Psigning.password=${GPG_KEY_PASSPHRASE} -Psigning.secretKeyRingFile=secring.gpg
fi


