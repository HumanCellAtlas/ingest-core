#!/usr/bin/env bash

if [ -z "${METRICS_PREFIX}" ]; then
  METRICS_PREFIX=ingest_core
fi

if [ -z "${METRICS_DIR}" ]; then
  METRICS_DIR=$PWD/metrics
fi

function count_fd
{
  PID=$(jcmd -l | awk 'NR==1 {print $1}')
  FD_COUNT=$(lsof -p ${PID} | wc -l)
  echo ${FD_COUNT}
}

function log_fd_count {
  if [ ! -f ${METRICS_DIR} ]; then
    mkdir -p ${METRICS_DIR}
  fi
  ENTRY="${METRICS_PREFIX}_open_fd $(count_fd) $(date +%s)"
  echo ${ENTRY} 1> ${METRICS_DIR}/fd_count.prom
}

log_fd_count
