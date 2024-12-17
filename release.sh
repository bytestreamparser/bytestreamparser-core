#!/bin/bash

SOURCE="${SOURCE:-$(git rev-parse --abbrev-ref HEAD)}"
TIMESTAMP="$(git log -1 --pretty=%cd --date=format:'%Y%m%d%H%M%S')"
COMMIT="$(git rev-parse --short HEAD)"
REVISION="${REVISION:-0.$SOURCE.$TIMESTAMP.$COMMIT}"

mvn --define revision="$REVISION" --batch-mode clean deploy 1>&2

echo "REVISION=$REVISION"
