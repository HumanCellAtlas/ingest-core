#!/usr/bin/env bash

if [ -z "${METRICS_SCHEDULE}" ]; then
  METRICS_SCHEDULE=0 0 * * * # every 12AM
fi

(crontab -l 2> /dev/null; echo "${METRICS_SCHEDULE} /opt/metrics/metrics.sh") | sort -u - |
crontab -