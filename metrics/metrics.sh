#!/usr/bin/env bash

if [ -z "${METRICS_PREFIX}" ]; then
  METRICS_PREFIX=ingest_core
fi

function count_fd
{
  PID=$(jcmd -l | awk 'NR==1 {print $1}')
  FD_COUNT=$(lsof -p ${PID} | wc -l)
  echo $FD_COUNT
}

function fd_prom {
  echo "${METRICS_PREFIX}_open_fd $(count_fd) $(date +%s)"
}

fd_prom
