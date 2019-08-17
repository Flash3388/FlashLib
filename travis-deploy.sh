#!/usr/bin/env bash
./gradlew publish -PNEXUS_USERNAME=${NEXSUS_USERNAME} -PNEXUS_PASSWORD=${NEXUS_PASSWORD} -Psigning.keyId=${GPG_KEY_ID} -Psigning.password=${GPG_KEY_PASSPHRASE} -Psigning.secretKeyRingFile=secring.gpg


