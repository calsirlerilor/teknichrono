#!/bin/bash
DIR="$( cd "$( dirname "$0" )" && pwd )"

set -e

export TEKNICHRONO_HOST=${1:-localhost:8080}

bash $DIR/moto_tests.sh
bash $DIR/snowscoot_tests.sh 