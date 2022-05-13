/*
 * Copyright 2022 znai maintainers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, { useEffect } from "react";

import { configuredEcharts } from "./EchartsCommon";
import { useRef } from "react";

const echarts = configuredEcharts();

interface Props {
  labels: string[];
  chartType: string;
  data: any[][];
  height?: number;
}

export function EchartGeneric({ labels, chartType, data, height }: Props) {
  const echartRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const bar = echarts.init(echartRef.current!);
    const series = [];
    for (let colIdx = 1; colIdx < labels.length; colIdx++) {
      series.push(createSeriesInstance(colIdx));
    }

    bar.setOption({
      tooltip: {},
      xAxis: {
        data: data.map((row) => row[0]),
      },
      animation: false,
      yAxis: {},
      series: series,
    });

    function createSeriesInstance(columnIdx: number) {
      return {
        name: labels[columnIdx],
        type: chartType,
        data: data.map((row) => row[columnIdx]),
      };
    }
  }, [chartType, labels, data, height]);

  const heightToUse = height || 400;

  return <div className="content-block" ref={echartRef} style={{ height: heightToUse }} />;
}
