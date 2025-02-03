package com.revoola.services

import org.json.JSONArray

object RLAllHTMLChart {

    fun RLgetEffortChartHtml(jsondata: String) : String {
        val webdata:String="""
            <!DOCTYPE html>
        <meta charset="utf-8">
        <title>Line Chart</title>
        <style> /* set the CSS */

                body { font: 30px Arial;background-color: #FFFFFFFF;}

                path {
                            stroke: steelblue;
                            stroke-width: 2;
                            fill: none;
                        }

                        .axis path,
                        .axis line {
                            fill: none;
                            stroke: grey;
                            stroke-width: 1;
                            shape-rendering: crispEdges;
                        }
                .axis RLText{
                  fill: grey;
                  color: grey;
                }
                        .line {
                            fill: none;
                            stroke: url(#line-gradient);
                            stroke-width: 3px;
                        }

                    .dot {
                            fill: url(#line-gradient);
                        }

                        </style>
                <body>
                <div id="lineChart"></div>


                <!-- load the d3.js library -->
                <script src="https://cdnjs.cloudflare.com/ajax/libs/d3/3.5.17/d3.min.js"></script>

                <script>

                // Set the dimensions of the canvas / graph
                var margin = {top: 100, right: 20, bottom: 100, left: 50},
                    width = window.innerWidth - margin.left - margin.right,
                    height = window.innerHeight - margin.top - margin.bottom;

                // Set the ranges
                var x = d3.scale.linear().range([0, width]);
                var y = d3.scale.linear().range([height, 0]);

                // Define the axes
                var xAxis = d3.svg.axis().scale(x)
                    .orient("bottom").outerTickSize(0).innerTickSize(-height, 0, 0).ticks(6);
                var yAxis = d3.svg.axis().scale(y)
                    .orient("left").ticks(5).outerTickSize(0).innerTickSize(-width, 0, 0).ticks(10);

                // Define the line
                var valueline = d3.svg.line().interpolate("linear")
                    .x(function(d) { return x(d.time); })
                    .y(function(d) { return y(d.effort); });
                    
                // Adds the svg canvas
                var svg = d3.select("#lineChart")
                    .append("svg")
                        .attr("width", width + margin.left + margin.right)
                        .attr("height", height + margin.top + margin.bottom)
                    .append("g")
                        .attr("transform",
                              "translate(" + margin.left + "," + margin.top + ")");



                    var data = JSON.parse('$jsondata');
                    data.forEach(function(d) {
                            d.time = +d.time/60;
                            d.effort = +d.effort;
                        });
                
                        // Scale the range of the data
                        x.domain([d3.min(data, function(d) { return +d.time; }), d3.max(data, function(d) { return +d.time; })]);
                        y.domain([0, 100]);
                
                        svg.append("linearGradient")
                            .attr("id", "line-gradient")
                            .attr("gradientUnits", "userSpaceOnUse")
                            .attr("x1", 0).attr("y1", y(0))
                            .attr("x2", 0).attr("y2", y(100))
                        .selectAll("stop")
                            .data([
                                {offset: "0%", color: "rgb(241, 119, 160)"},
                                {offset: "29.99%", color: "rgb(241, 119, 160)"},
                                {offset: "30%", color: "rgb(255, 207, 47)"},
                                {offset: "49.99%", color: "rgb(255, 207, 47)"},
                                {offset: "50%", color: "rgb(44, 174, 44)"},
                                {offset: "59.99%", color: "rgb(44, 174, 44)"},
                                {offset: "60%", color: "rgb(0, 153, 218)"},
                                {offset: "69.99%", color: "rgb(0, 153, 218)"},
                                {offset: "70%", color: "rgb(254, 105, 02)"},
                                {offset: "79.99%", color: "rgb(254, 105, 02)"},
                                {offset: "80%", color: "rgb(153, 0, 204)"},
                                {offset: "89.99%", color: "rgb(153, 0, 204)"},
                                {offset: "90%", color: "rgb(237, 69, 65)"},
                                {offset: "99.99%", color: "rgb(237, 69, 65)"}
                            ])
                        .enter().append("stop")
                            .attr("offset", function(d) { return d.offset; })
                            .attr("stop-color", function(d) { return d.color; });
                
                        // Add the valueline path.
                        svg.append("path")
                            .attr("class", "line")
                            .attr("d", valueline(data));
                
                        // Add the X Axis
                        var xx = svg.append("g")
                            .attr("class", "x axis")
                            .attr("transform", "translate(0," + height + ")")
                            .call(xAxis);
                
                        // Add the Y Axis
                        var yx = svg.append("g")
                            .attr("class", "y axis")
                            .call(yAxis);
                        
                            xx.selectAll("line").remove();
                            
                            yx.selectAll("line").style("opacity", 0.6);
                            yx.selectAll("RLText").attr("x", -5);
                
                            var last = svg.selectAll(".y .tick RLText")[0].length;
                
                            svg.selectAll(".y .tick RLText")[0].forEach(function(d, i){
                            if (i==last-1){
                                return;
                            }
                            if(i%2!=0)
                                d3.select(d).style("display", "none")
                            });
                
                            svg.append("RLText").attr("x", (width/2)-100).attr("y", height+80).style("font-size", 40).style("fill", "grey").RLText("MINUTES");
                            svg.append("RLText").attr("x", 0).attr("y", -20).style("font-size", 40).style("fill", "grey").RLText("EFFORT%");

    </script>
    </body>
</html>           
""".trimIndent()
        return webdata
    }
    fun RLgetElevationHtml(arrCumDistance:String, arrElevation:String): String{
        return """
            <!DOCTYPE html>
            <head>
                <meta charset="utf-8">
                <title>Area Chart</title>
                <style>
                    line{
                        stroke-opacity: 0.5;
                        stroke: grey;
                    }
                    .xaxis .tick line{
                        stroke-opacity: 0;
                        stroke: grey;
                    }
                    path.domain{
                        stroke: grey;
                        stroke-width: 1.5;
                    }
                    .yaxis RLText{
                        font-size: 30px;
                        fill: grey;
                    }
                    .xaxis RLText{
                        font-size: 30px;
                        fill: grey;
                    }
                    table {
              width: 100%;
            }
                </style>
                <!-- Load d3.js -->
                <script src="https://d3js.org/d3.v4.js"></script>
            </head>
            <body>
                <!-- Create a div where the graph will take place -->
                <div id="my_dataviz"></div>

                <script>

                    // set the dimensions and margins of the graph
                    var margin = {top: 70, right: 30, bottom: 80, left: 50},
                        width = window.innerWidth - margin.left - margin.right,
                        height = window.innerHeight - margin.top - margin.bottom;
                    
                    // append the svg object to the body of the page
                    var svg = d3.select("#my_dataviz")
                      .append("svg")
                        .attr("width", width + margin.left + margin.right)
                        .attr("height", height + margin.top + margin.bottom)
                        .style("overflow", "visible")
                        .append("g")
                        .attr("transform",
                              "translate(" + margin.left + "," + margin.top + ")");
                    var arrCumDistance=JSON.parse('$arrCumDistance');
                   
                    var arrElevation=JSON.parse('$arrElevation');
                   
                    var iterations = 0;
                    if(arrElevation.length<arrCumDistance.length||arrElevation.length==arrCumDistance.length){
                        iterations = arrElevation.length;
                    }
                    else{
                        iterations = arrCumDistance.length;
                    }
                    var data = [];
                    for(var i = 0; i < iterations; i++){
                        var obj = {};
                        obj.arrCumDistance = +arrCumDistance[i];
                        obj.arrElevation = +arrElevation[i];
                        data.push(obj);
                    }
                    
                        // Add X axis
                        var x = d3.scaleLinear()
                        .domain(d3.extent(data, function(d) { return +d.arrCumDistance; }))
                          .range([ 0, width ]);
                        svg.append("g")
                        .attr("class", "xaxis")
                          .attr("transform", "translate(0," + height + ")")
                          .call(d3.axisBottom(x).ticks(8).tickSizeOuter([0]))
                        svg.append("RLText")
                          .attr("x", width/2)
                          .attr("y", height+58)
                          .attr("font-size", 30)
                        .style("font-family", "omnes-pro, sans-serif")
                          .RLText("KM's")
                    
                        let minimum = d3.min(data, function (d) { return +d.arrElevation; });
                        let maximum = d3.max(data, function (d) { return +d.arrElevation; });
                        let difference = maximum - minimum;
                        let onepercent = difference/100;
                        let fifteenpercent = onepercent * 15;
                        let upperLimit = minimum - fifteenpercent;
                        let lowerLimit = maximum + fifteenpercent;
                        
                        // Add Y axis
                        var y = d3.scaleLinear()
                          .domain([upperLimit, lowerLimit])
                          .range([ height, 0 ]);
                        svg.append("g")
                        .attr("class", "yaxis")
                          .call(d3.axisLeft(y).ticks(9).tickSizeInner([-width]).tickSizeOuter([0]));
                        svg.append("RLText")
                            .attr("y", -5)

                            .attr("x", -31)
                            .attr("font-size", 30)
            .style("font-family", "omnes-pro, sans-serif")
                            .RLText("METERS");
                        // Add the area
                        svg.append("path")
                          .datum(data)
                          .attr("fill", "lightgreen")
                          .attr("fill-opacity", 0.5)
                          .attr("stroke", "#69b3a2")
                          .attr("stroke-width", 0.5)
                          .attr("d", d3.area()
                            .x(function(d) { return x(d.arrCumDistance) })
                            .y0(y(d3.min(data, function(d) { return +d.arrElevation - fifteenpercent ; })))
                            .y1(function(d) { return y(d.arrElevation) })
                            )
                    
                    </script>
            </body>
            </html>
        """.trimIndent()
    }
    fun RLgetSpeedHtml(arrCumDistance:String, arrElevation:String, arrCumSpeed:String): String {
        return """
                    <!DOCTYPE html>
                    <head>
                      <meta charset="utf-8">
                      <title>Multi Area Chart</title>
                      <script src="https://d3js.org/d3.v4.min.js"></script>
                      <style>
                        body { margin:0;position:fixed;top:0;right:0;bottom:0;left:0; }
                      </style>
                    </head>

                    <body>
                      <script>
                       var arrCumDistance= JSON.parse('$arrCumDistance');
                       var arrElevation=JSON.parse('$arrElevation');
                       var arrCumSpeed=JSON.parse('$arrCumSpeed');
                       
                        var iterations = 0;
                            console.log(arrCumDistance.length)
                            console.log(arrElevation.length)
                                        if(arrElevation.length<arrCumDistance.length||arrElevation.length==arrCumDistance.length){
                                            iterations = arrElevation.length;
                                        }
                                        else{
                                            iterations = arrCumDistance.length;
                                        }
                                        var data = [];
                                        for(var i = 0; i < iterations; i++){
                                            var obj = {};
                                            obj.arrCumDistance = +arrCumDistance[i];
                                            obj.arrCumSpeed = +arrCumSpeed[i];
                                            obj.arrElevation = +arrElevation[i];
                                            data.push(obj);
                                        }
                                        data.columns = d3.keys(data[0]);
                                        console.log(data)

                                const margin = { left: 55, right: 0, top: 35, bottom: 75 };
                                const width = window.innerWidth-50;
                                const height = window.innerHeight - margin.top - margin.bottom;
                                var svg = d3.select('body').append('svg').attr('width', width).attr('height', height).style("overflow", "visible")

                                const xScale = d3.scaleLinear().range([ margin.left, width - margin.right, ]).domain(d3.extent(data, stock => stock.arrCumDistance));
                                let minimum = d3.min(data, function (d) { return +d.arrElevation; });
                                  let maximum = d3.max(data, function (d) { return +d.arrElevation; });
                                  let difference = maximum - minimum;
                                  let onepercent = difference / 100;
                                  let fifteenpercent = onepercent * 15;
                                  let upperLimit = minimum - fifteenpercent;
                                  let lowerLimit = maximum + fifteenpercent;
                                  let heightBottom = height - margin.bottom;

                                const y1Scale = d3.scaleLinear().range([ height - margin.bottom, margin.top, ]).domain([ upperLimit, lowerLimit]);
                                const y2Scale = d3.scaleLinear().range([ height - margin.bottom, margin.top, ]).domain([ d3.min(data, stock => stock.arrCumSpeed), d3.max(data, stock => stock.arrCumSpeed)]);
                                const xAxis = svg.append('g').attr("class", "axis").style("font-size", 34).attr('transform', `translate(0,`+ heightBottom +`)`).call(d3.axisBottom(xScale).ticks(7).tickSizeOuter([0]).tickSizeInner([0]).tickPadding(10));
                                const elivationArea = d3.area().x (d => xScale(d.arrCumDistance)).y0(d => height - margin.bottom).y1(d => y1Scale(d.arrElevation));
                                const speedArea = d3.area().x (d => xScale(d.arrCumDistance)).y0(d => height - margin.bottom).y1(d => y2Scale(d.arrCumSpeed)).curve(d3.curveBasis);
                                const speedLine = d3.line().x(d => xScale(d.arrCumDistance)).y(d => y2Scale(d.arrCumSpeed));
                                  
                                svg.append('path').data([data]).attr('d', elivationArea).attr('fill', 'rgb(228, 235, 230)').attr('opacity', 0.6);
                                    //svg.append('path').attr('d', speedLine(data)).attr('stroke', 'rgba(0, 188, 212, 0.47)').attr('stroke-width', 3).attr('fill', 'none');
                                    svg.append('path').data([data]).attr('d', speedArea).attr('fill', 'rgba(0, 188, 212, 0.47)').attr('opacity', 0.6);
                                    svg.append("RLText").attr("x", width/2).attr("y", height - 5 ).style("font-size", 34).style("font-family", "omnes-pro, sans-serif").RLText("KM's");
                                    
                                const y1Axis = svg.append('g').attr("class", "axis").style("font-size", 34).attr('transform', `translate(`+ margin.left +`, 0)`).call(d3.axisLeft(y2Scale).ticks(5).tickSizeOuter([0]).tickSizeInner([0]).tickPadding(10)).append("RLText")
                                  .attr("x", 40).attr("y", 1).attr("dy", "0.71em").attr("fill", "#000").style("font-size", 34).style("font-family", "omnes-pro, sans-serif").RLText("KMH");
                                
                              </script>
                            </body>

                        """.trimIndent()
    }
    fun RLgetPaceChartHtml(jsondata: String) : String{

        return """
           <!DOCTYPE html>
                <html>
                            <head>
                                <script src="https://d3js.org/d3.v4.min.js"></script>
                                <style>body {
                                        margin: 0
                                    }
                                    .domain {
                                        display: none
                                    }

                                    .tick line {
                                        stroke: #c0c0bb
                                    }

                                    .tick RLText {
                                        fill: #8e8883;
                                        font-size: 32pt;
                                        font-family: sans-serif
                                    }
                                    .axis-label {
                                        fill: #635f5d;
                                        font-size: 32pt;
                                        font-family: sans-serif
                                    }
                                    </style>
                            </head>
                            <body>
                                <div id="chart"></div>
                            <script>
                            
                    

                //Here is the time in seconds
                  var timeData = JSON.parse('$jsondata');
               
                var arrCumDistance = [];

                                timeData.forEach((d,i)=>{
                                  arrCumDistance.push(i);
                                })

                                var distanceData = arrCumDistance;
                                        distanceData.sort(function(a, b){return a - b});
                                        var barData = [];
                                        var total = 0;
                                        distanceData.forEach(function(d){
                                            var obj = {}
                                            obj.arrCumDistance = Math.trunc(d);
                                            barData.push(obj)
                                        })
                                        var buckets = [...new Set(barData.map(d => d.arrCumDistance))];
                                        
                                        
                                        var finalBarData = [];
                                        buckets.forEach(function(b, i){
                                            var fdata = barData.filter(function(d){ return d.arrCumDistance == b;})
                                            var obj = {};
                                            obj.arrCumDistance = +b+1;
                                            obj.time = timeData[i];
                                            finalBarData.push(obj)
                                        })

                                      var compkilometers = [];
                                      for(i=0;i<finalBarData.length-1;i++){
                                        compkilometers.push(finalBarData[i])
                                      }
                                      
                                      var mintimeseconds = d3.min(compkilometers, function(d){ return d.time;})
                                      const noOfBars = buckets.length;
                                      const xValue = d => d.time;;
                                      const yValue = d => +d.arrCumDistance;
                                      const margin = { left: 50, right: 140, top: 35, bottom: 75 };
                                      const barHeight = 60;
                                      const width = innerWidth;
                                      const height = barHeight*noOfBars+200;
                                      const innerWidthA = width - margin.left - margin.right;
                                      const innerHeight = height - margin.top - margin.bottom;
                                      const svg = d3.select("#chart").append("svg").attr("height", height).attr("width", width);

                                   
                                   const g = svg.append("g")
                                  .attr("transform", `translate(`+ margin.left +`,`+  margin.top +`)`);
                                  const xAxisG = g.append("g")
                                      .attr("transform", `translate(0,` + innerHeight + `)`);
                                       
                                      const yAxisG = g.append("g");

                                      const xScale = d3.scaleLinear();
                                      const yScale = d3.scaleBand()
                                        .paddingInner(0.3)
                                        .paddingOuter(0);

                                      const xTicks = 10;
                                      const xAxis = d3.axisBottom()
                                        .scale(xScale)
                                        .ticks(xTicks)
                                        .tickPadding(5)
                                        .tickSize(-innerHeight);

                                      const yAxis = d3.axisLeft()
                                        .scale(yScale)
                                        .tickPadding(5)
                                        .tickSize(-innerWidthA);

                                        var data = finalBarData;
                                      console.log(data);
                                        yScale
                                          .domain(data.map(yValue).reverse())
                                          .range([innerHeight, 0]);

                                        xScale
                                          .domain([0, d3.max(data, xValue)])
                                          .range([0, innerWidthA])
                                          .nice(xTicks);

                                        var bars = g.selectAll("rect").data(data)
                                          .enter().append("rect")
                                            .attr("x", 0)
                                            .attr("y", d => yScale(yValue(d)))
                                            .attr("width", d => xScale(xValue(d)))
                                            .attr("height", barHeight)
                                            .attr("rx", 12)
                                            .attr("fill", "#39B54A");
                                        
                                        xAxisG.call(xAxis);

                                        yAxisG.call(yAxis);
                                        yAxisG.selectAll(".tick line").remove();
                                        xAxisG.selectAll(".tick RLText").remove();

                                        function toTimeString(seconds) {
                                            return getReadableTime(seconds);
                                        }
                                        var timeAxisData = finalBarData;
                                        timeAxisData.forEach(function(t){
                                            t.time = toTimeString(t.time);
                                        })
                                        
                                        g.selectAll("label").data(timeAxisData)
                                          .enter().append("RLText")
                                            .attr("class", "label")
                                            .attr("y", d => yScale(yValue(d))+40)
                                            .attr("x", +innerWidthA+15)
                                            .attr("font-size", 32)
                                            .attr("font-family", 'sans-serif')
                                            .attr("fill", "#8E8883")
                                            .RLText(function (d) {
                                                return d.time;
                                            });
                                      var totaltimeofcompkilo = 0;
                                      for(i=0;i<buckets.length-1;i++){
                                        var filterdata = arrCumDistance.filter(function(d){ return Math.trunc(d)==i; });
                                        totaltimeofcompkilo = totaltimeofcompkilo + filterdata.length;
                                      }
                                      
                                      var avgtimeseconds = totaltimeofcompkilo/(compkilometers.length);
                                      var mintime = toTimeString(mintimeseconds);
                                      var avgtime = toTimeString(avgtimeseconds);
                                      
                                      
                                         function getReadableTime(sec) {
                                            var hrs = Math.floor(sec / 3600);
                                            var min = Math.floor((sec - (hrs * 3600)) / 60);
                                            var seconds = sec - (hrs * 3600) - (min * 60);
                                            seconds = Math.round(seconds * 100) / 100
                                           
                                           var result = "";
                                            if(hrs > 0){
                                                result += (hrs < 10 ? hrs : hrs) + ":";
                                            }
                                            result += (min < 10 ? "0" + min : min);
                                            result += ":" + (seconds < 10 ? "0" + seconds : seconds);
                                            return result;
                                         }
                                      </script>
                                </body>
                                </html>  
        """.trimIndent()
    }
    fun RLgetChallengeChartHtml (steps_so_far:Int,target_steps:Int,time_gone:Int,total_time:Int) : String{
        return """
         <!DOCTYPE html>
         <html>
         <head>
             <meta charset="utf-8">
             <title>Progress</title>
             <style>
                 body { margin:0; position:fixed; top:0; right:0; bottom:0; left:0; }
                 .yaxis path { stroke: none; }
                 .yaxis line { stroke: none; }
                 text { fill: #aaa; font-size: 40px; font-family: sans-serif; }
                 .yaxis text { fill: #aaa; font-size: 50px; font-family: sans-serif; }
             </style>
             <!-- Load d3.js -->
             <script src="https://d3js.org/d3.v4.js"></script>
         </head>
         <body>
         <script>
//             let steps_so_far = '$steps_so_far'; //3364 number of steps taken
//             let target_steps = '$target_steps'; // 3333 total number of steps
//             let time_gone ='$time_gone'; //0 number of days passed away
//             let total_time = '$total_time'; //1total number of days for completing all steps
             
               let steps_so_far = $steps_so_far; //3364 number of steps taken
               let target_steps = $target_steps; // 3333 total number of steps
               let time_gone =$time_gone; //0 number of days passed away
               let total_time = $total_time; //1total number of days for completing all steps
             
             if(steps_so_far > target_steps) {
                 steps_so_far = target_steps;
             }
             if(time_gone > total_time || time_gone === 0.0) {
                 time_gone = total_time;
             } else {
                 time_gone = (total_time - time_gone);
             }
             
             const margin = { left: 35, right: 0, top: 35, bottom: 35 };
             const chart_width = window.innerWidth;
             const chart_height = window.innerHeight;
             const width = (chart_width / 100) * 20; // Width of the progress bar
             const height = chart_height - margin.top - margin.bottom; // Height of the progress bar

             // Create an SVG container
             const svg = d3.select('body').append('svg')
                 .attr("width", chart_width)
                 .attr("height", chart_height);

             let x_mid = chart_width / 2 - (width / 2);
             let shift_percentage = 45; // Increased percentage for more separation
             let step_x = x_mid - ((x_mid / 100) * shift_percentage);
             let time_x = x_mid + ((x_mid / 100) * shift_percentage);

             var y = d3.scaleLinear().range([height, 0]);
             var yAxis = d3.axisRight(y).ticks(3).tickValues([25, 50, 75]).tickFormat(d => d + "%");
             y.domain([0, 100]);

             svg.append("g").attr("transform", "translate(" + (x_mid + 60) + "," + 50 + ")")
                 .attr("class", "yaxis").call(yAxis);

             // Adjust text positions
             svg.append("text")
                 .attr("x", step_x + width / 2)
                 .attr("y", 40)
                 .attr("text-anchor", "middle")
                 .text("STEPS");

             svg.append("text")
                 .attr("x", time_x + width / 2)
                 .attr("y", 40)
                 .attr("text-anchor", "middle")
                 .text("TIME");

             const progressBar = svg.append("g").attr("transform", "translate(" + step_x + "," + 50 + ")");
             progressBar.append("rect")
                 .attr("width", width)
                 .attr("height", height)
                 .attr("rx", 12)
                 .attr("fill", "#ccc");

             progressBar.append("rect")
                 .data([steps_so_far / target_steps])
                 .attr("width", width)
                 .attr("height", 0)
                 .attr("y", height)
                 .attr("rx", 12)
                 .attr("fill", "#4CAF50")
                 .transition()
                 .duration(1000)
                 .attr("height", d => height * d)
                 .attr("y", d => height * (1 - d));

             const progressBar1 = svg.append("g").attr("transform", "translate(" + time_x + "," + 50 + ")");
             progressBar1.append("rect")
                 .attr("width", width)
                 .attr("height", height)
                 .attr("rx", 12)
                 .attr("fill", "#ccc");

             progressBar1.append("rect")
                 .data([time_gone / total_time])
                 .attr("width", width)
                 .attr("height", 0)
                 .attr("y", height)
                 .attr("rx", 12)
                 .attr("fill", "#118def")
                 .transition()
                 .duration(1000)
                 .attr("height", d => height * d)
                 .attr("y", d => height * (1 - d));
         </script>
         </body>
         </html>

        """.trimIndent()
    }
    fun RLgetChallengeSessionChartHtml (steps_so_far:Int,target_steps:Int) : String{
        return """
       
       <!DOCTYPE html>
       <html>
       <head>
           <meta charset="utf-8">
           <title>Progress</title>
           <style>
               body {
                   margin: 0;
                   position: fixed;
                   top: 0;
                   right: 0;
                   bottom: 0;
                   left: 0;
               }
               text {
                   fill: #aaa;
                   font-size: 40px;
                   font-family: sans-serif;
               }
           </style>
           <!-- Load d3.js -->
           <script src="https://d3js.org/d3.v4.js"></script>
       </head>
       <body>
           <script>
               let steps_so_far = '$steps_so_far'; // \(achieved);  // Number of steps taken
               let target_steps ='$target_steps';  // \(target);  // Total number of steps target

              const margin = { left: 35, right: 35, top: 50, bottom: 35 };
                      const chart_width = window.innerWidth - margin.left - margin.right;
                      const chart_height = window.innerHeight - margin.top - margin.bottom;
                      const bar_width = 250;  // Width of each progress bar
                      const gap = 100;  // Gap between bars

                      // Create an SVG container
                      const svg = d3.select('body').append('svg')
                          .attr("width", window.innerWidth)
                          .attr("height", window.innerHeight);

                      // Calculate bar positions
                      let x_firstBar = (window.innerWidth / 2) - bar_width - (gap / 2);
                      let x_secondBar = (window.innerWidth / 2) + (gap / 2);

                      // Scale for the bars
                      var y = d3.scaleLinear()
                          .range([chart_height, 0])
                          .domain([0, Math.max(steps_so_far, target_steps)]);

                      // First Bar: Steps Achieved
                      const achievedBar = svg.append("g")
                          .attr("transform", `translate(`+ x_firstBar +`, `+margin.top+`)`);
                      achievedBar.append("rect")
                          .attr("width", bar_width)
                          .attr("height", chart_height)
                          .attr("rx", 12)  // Rounded corners
                          .attr("fill", "#ccc");  // Background color

                      achievedBar.append("rect")
                          .attr("width", bar_width)
                          .attr("height", y(0) - y(steps_so_far))
                          .attr("y", y(steps_so_far))
                          .attr("rx", 12)  // Rounded corners
                          .attr("fill", "#4CAF50");  // Fill color

                      achievedBar.append("text")
                          .attr("x", bar_width / 2)
                          .attr("y", -20)
                          .attr("text-anchor", "middle")
                          .text("ACHIEVED");

                      // Second Bar: Target Steps
                      const targetBar = svg.append("g")
                          .attr("transform", `translate(` + x_secondBar + `, `+ margin.top +`)`);
                      targetBar.append("rect")
                          .attr("width", bar_width)
                          .attr("height", chart_height)
                          .attr("rx", 12)  // Rounded corners
                          .attr("fill", "#ccc");  // Background color

                      targetBar.append("rect")
                          .attr("width", bar_width)
                          .attr("height", y(0) - y(target_steps))
                          .attr("y", y(target_steps))
                          .attr("rx", 12)  // Rounded corners
                          .attr("fill", "#118def");  // Fill color

                      targetBar.append("text")
                          .attr("x", bar_width / 2)
                          .attr("y", -20)
                          .attr("text-anchor", "middle")
                          .text("TARGET");
                  </script>
              </body>
              </html>
        """.trimIndent()
    }

    fun RLgetRankingChartHtmlOld(jsondata: JSONArray, userid:String): String{
        return """     
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="utf-8">
                <title>Bar</title>
                <style>
                    body {
                        margin: 0;
                        top: 0;
                        right: 0;
                        bottom: 0;
                        left: 0;
                    }
                    .image {
                        stroke: white;
                        border-width: 1px;
                        border-radius: 50%;
                        overflow: hidden;
                    }
                    .x-axis text{
                        fill: none;
                        font-family: "Omnes";
                    }
                    .x-axis path{
                        stroke: none;
                    }

                    .x-axis line{
                        stroke: white;
                    }
                </style>
                <!-- Load d3.js -->
                <script src="https://d3js.org/d3.v7.js"></script>
            </head>

            <body>
                <script>
                    

          
         
                
               // Define your data (sample data)
                       var data = $jsondata;
                       
                       
            		var bar_color = "#ebf7ed";
                    var percentage_label_color = "#4a4c4f";
                    var user_name_label_color = "#4a4c4f"; // Change user_name_label_color to white
                    var name_label_color = "#4a4c4f";
                    var index_label_color = "black";
                    var name_font_size = "45px";
                    var percentage_font_size = "45px";
                    var index_font_size = "50px";
                    var font_family = "Omnes, sans-serif";
                    var barHeight = 65;
                    var user_name ='$userid';
                    var font_vertical_pos = 13;

              
                   	 // Sort data by value in descending order
                   data.sort((a, b) => b.value - a.value);




                    // Find the index of the user's bar
                    const userIndex = data.findIndex(d => d.userid === user_name);

                    // Determine which bars to display
                    let displayData=data;
                    //if (userIndex <= userIndex) {
                      //  displayData = data;
            			
            //			.slice(0, userIndex);
                    //} else {
                      //  displayData = data.slice(0, 14).concat(data[userIndex]);
                    //}
            		// Now, sort displayData in ascending order based on the value
            //displayData.sort((a, b) => a.value - b.value);
            	
                    const svgWidth = (window.innerWidth / 100) * 90;
                    //const svgHeight = data.length * 90;
            		const svgHeight = data.length > 1 ? data.length * 90 : 180; // Adjust overall SVG height if only one bar
                    const margin = {top: 20, right: 80, bottom: 40, left: 120};
                    const width = svgWidth - margin.left - margin.right;
                    const height = svgHeight - margin.top - margin.bottom;

                    const svg = d3.select("body")
                            .append("svg")
                            .attr("width", svgWidth)
                            .attr("height", svgHeight)
                            .style("overflow", "visible")
                            .append("g")
                            .attr("transform", `translate(`+ margin.left +`,`+ margin.top +`)`);
            		

                    
                    // Set the ranges
                 //   const x = d3.scaleLinear().range([0, width]);
                 //   const y = d3.scaleBand().range([height, 0]).padding(0.1);
            			
            				        // Create scales
                    const xScale = d3.scaleLinear()
                            .domain([0, d3.max(data, d => d.value)])
                            .range([0, width]);

                    const yScale = d3.scaleBand()
                            .domain(displayData.map(d => d.name))
                            .range([0, height])
                            .padding(0.25);

                    // Add axes if needed
                    const xAxis = d3.axisBottom(xScale).tickSize(-height);
                    svg.append("g")
                            .attr("class", "x-axis")
                            .attr("transform", `translate(0, `+ height +`)`)
                            .call(xAxis);

                    const yAxis = d3.axisLeft(yScale);
                    svg.append("g")
                            .attr("class", "y-axis")
                            .style("display", "none")
                            .call(yAxis);

                    // Create bars
                    svg.selectAll(".gap")
                            .data(displayData)
                            .enter()
                            .append("rect")
                            .attr("class", "gap")
                            .attr("x", 10)
                            .attr("y", d => yScale(d.name))
                            .attr("width", d => xScale(d.value))
                            .attr("height", yScale.bandwidth())
                            .attr("rx", 12)
                            //.style("fill", d => d.userid.startsWith(user_name) ? "#39b54a" : bar_color); // Dark blue color for user_name, bar_color for others
            				 .style("fill", (d, i) => i === userIndex ? "#39b54a" : bar_color); // Use index to determine fill color
                    svg
                            .append("defs")
                            .selectAll("pattern")
                            .data(displayData)
                            .enter()
                            .append("pattern")
                            .attr("id", (d, i) => "image-pattern-" + i)
                            .attr("width", 1) // Adjust the width of the pattern as needed
                            .attr("height", 1) // Adjust the height of the pattern as needed
                            .attr("x", 0)
                            .attr("y", 0)
                            .attr("patternContentUnits", "objectBoundingBox")
                            .append("image")
                            .attr("x", 0)
                            .attr("y", 0)
                            .attr("width", 1)
                            .attr("height", 1)
                            .attr("xlink:href", (d) => d.image)
                            .attr("preserveAspectRatio", "xMinYMin slice");

                    svg.selectAll(".circle")
                            .data(displayData)
                            .enter()
                            .append("circle")
                            .attr("class", "circle")
                            .attr("cx", -margin.left + 60)
                            .attr("cy", d => yScale(d.name) + yScale.bandwidth() / 2 + 2)
                            .attr("r", yScale.bandwidth() / 2) //
                            .style("fill", (d, i) => `url(#image-pattern-`+ i+`)`);
            				
            				
            				
            				// This code goes after the section where you create the bars.

            if (userIndex > userIndex) {
                // Calculate the y-position of the 14th bar. Since bars are drawn using yScale,
                // you can use yScale to find the y-position of the 14th item's name.
                // We add half of the bandwidth to position the line below the 14th bar.
                const yPositionOf14thBar = yScale(displayData[13].name) + yScale.bandwidth();

                // Draw a horizontal line at the y-position of the 14th bar
                svg.append("line")
                    .style("stroke", "black") // Color of the line
                    .style("stroke-width", 4) // Thickness of the line
                     .style("stroke-dasharray", "10,10") // Make the line dashed: 10 pixels dash, 10 pixels gap
            		.attr("x1", 0) // Starting x-position of the line
                    .attr("y1", yPositionOf14thBar+25) // Starting y-position of the line (same as ending y-position)
                    .attr("x2", width) // Ending x-position of the line
                    .attr("y2", yPositionOf14thBar+25); // Ending y-position of the line
            }

            				
            				
            				
            // metric values at right hand end of bars
               svg.selectAll(".label3")
                            .data(displayData)
                            .enter()
                            .append("text")
                            .attr("class", "label")
                            .attr("x", d => width - 10) // Adjust the position to right-align the text
                            .attr("y", d => yScale(d.name) + yScale.bandwidth() / 2 + font_vertical_pos + 4) // Center the text vertically
                            .style("fill", (d) => {
                                if (d.userid.startsWith(user_name)) {
                                    return user_name_label_color;
                                } else {
                                    return name_label_color;
                                }
                            })
                            .style("font-size", name_font_size)
                            .style("font-family", font_family)
                            .style("text-anchor", "end") // Right-align the text
                            .text(d => d.number2);

            // right hand side % labels

               svg.selectAll(".label4")
                            .data(displayData)
                            .enter()
                            .append("text")
                            .attr("class", "label")
                            .attr("x", width + 150) // Adjust the position to right-align the text
                            .attr("y", d => yScale(d.name) + yScale.bandwidth() / 2 + font_vertical_pos + 4)
                            .style("fill", percentage_label_color)
                            .style("font-size", percentage_font_size)
                            .style("font-family", font_family)
                            .style("text-anchor", "end") // Right-align the text
                            .text(d => d.value + "%");

            // Find the index of the user in the data array
            var userRowIndex = (data.findIndex(d => d.userid === user_name)+1);
            	


            // labels in bars
            // Find the first index where the value is 0 to adjust ranking shown for users with value 0
            const firstZeroValueIndex = data.findIndex(d => d.value === 0) + 1; // Adding 1 for 1-based indexing


            // Determine the lowest ranking for users with a value of 0 among the bars
            let ranksWithZeroValue = data
              .map((d, i) => ({ value: d.value, rank: i + 1 }))
              .filter(d => d.value === 0 && d.rank <= userRowIndex)
              .map(d => d.rank);

            let lowestRankForZeroValue = Math.min(...ranksWithZeroValue, data.length + 1); // Use a fallback if no 0 values


            // labels in bars
            svg.selectAll(".label1")
                .data(displayData)
                .enter()
                .append("text")
                .attr("class", "label")
                .attr("x", d => 15)  // Position the label slightly right of the y-axis
                .attr("y", d => yScale(d.name) + yScale.bandwidth() / 2 + font_vertical_pos + 4)  // Vertically center the text in the bar
                .style("fill", (d, i) => (i === userIndex && d.value === 0) ? "#39b54a" : name_label_color)  // Change font color to blue if bar length is zero and it's the userIndex
                .style("font-size", name_font_size)
                .style("font-family", font_family)
                .text(d => {
                    // Determine the ranking directly based on displayData for simplicity and clarity
                    let ranking = displayData.findIndex(x => x.userid === d.userid) + 1;
                    return ``+ ranking +` - `+  d.name.split(" - ")[1]+``;  // Display ranking and name
                });
			   
                </script>
            </body>
            </html>
        """.trimIndent()
    }
    fun RLgetIndividualStepsChartHtmlOlD(jsondata: JSONArray): String{
        return """     
           <!DOCTYPE html>
           <html>
           <head>
             <script src="https://d3js.org/d3.v4.min.js"></script>
             <style>
               body {
                 font: 25px sans-serif;
               }
               svg {
                 font-family: Sans-Serif, Arial;
                 padding-bottom: 135px; /* Add padding-bottom to create space for x-axis labels and values */
               }
               .axis path,
               .axis line {
                 stroke: black;
               }
               .bar {
                 fill: #39B54A; /* Green color for the bars */
               }
               .tick line {
                 stroke-opacity: 0.99;
               }
             </style>
           </head>
           <body>
           <div id="chart"></div>
           <script>
               var array = [

                 {
                   name: "Challenge Metric",
                   values: $jsondata
                 }
               
           ];

           var challengeData = array.find(entry => entry.name === "Challenge Metric");
           createGraph(challengeData);

           function createGraph(data) {
             var margin = { top: 20, right: 20, bottom: 85, left: 100 },
                 barWidth = 56,
                 width = data.values.length * barWidth + margin.left + margin.right, // Adjust the overall width based on the number of bars
                 height = 600,
                 gapWidth = barWidth / 2; // Adjust gapWidth to ensure the first tick starts under the middle of the first bar

             /* Format Data */
             var parseDate = d3.timeParse("%d/%m/%Y");
             data.values.forEach(function(d) {
               d.date = parseDate(d.date);
               d.value = +d.value;
             });

             /* Scales */
             var xScale = d3.scaleBand()
                            .rangeRound([margin.left, width - margin.right])
                            .paddingInner(0.1)
                            .domain(data.values.map(d => d.date));

             var yScale = d3.scaleLinear()
                            .domain([0, d3.max(data.values, d => d.value)])
                            .range([height - margin.bottom, margin.top]);

             var svg = d3.select("#chart").append("svg")
                         .attr("width", width)
                         .attr("height", height);

             /* Add Axes */
             var xAxis = d3.axisBottom(xScale).tickFormat(d3.timeFormat("%d %b")),
                 yAxis = d3.axisLeft(yScale).ticks(10);
                var heightminusmarginbottom=height - margin.bottom
             svg.append("g")
                .attr("transform", `translate(0,`+heightminusmarginbottom+`)`)
                .call(xAxis)
                .selectAll("text")
                  .style("text-anchor", "end")
                  .attr("dx", "-.8em")
                  .attr("dy", ".15em")
           	   .style("font-size", 18)
                  .attr("transform", "rotate(-65)");

             svg.append("g")
                .attr("transform", `translate(`+ margin.left +`,0)`)
                .call(yAxis)
           	   .style("font-size", 18);

            /* Draw Bars with Rounded Tops Only */
           svg.selectAll(".bar")
              .data(data.values)
              .enter().append("path")
                .attr("d", function(d) {
                  const x = xScale(d.date) + gapWidth - 28; // Adjust the starting x position
                  const y = yScale(d.value);
                  const barHeight = height - margin.bottom - yScale(d.value);
                  const barWidth = xScale.bandwidth();
                   const ybarheight=y + barHeight
                    const barheightminusten= barHeight-10
                    const barwidthminustwenty= barWidth-20
                  
                  // Move to the bottom left, draw line up to the start of top left curve,
                  // arc for the top left corner, line across the top, arc for the top right corner,
                  // then line down the right side and close the path.
               
                 return `M`+x+`,`+ybarheight+` ` + // Move to bottom left
                    `v-`+ barheightminusten +` ` +   // Line up to start of top left curve
                    `q0,-10 10,-10 ` +         // Top left corner curve
                    `h`+barwidthminustwenty+` ` +     // Line across the top
                    `q10,0 10,10 ` +           // Top right corner curve
                    `v`+ barheightminusten +` ` +    // Line down the right side
                    `h-`+barWidth+`z`;          // Close path
                    })
                    .attr("fill", "#ebf7ed");


               /* Adjust label positions if you have them, assuming here how you might add them */
           	svg.selectAll(".bar-label").data(data.values).enter().append("text")
                .attr("class", "bar-label")
                .attr("x", d => xScale(d.date) + 26) // Adjusted for 40px offset
                .attr("y", d => yScale(d.value) - 5) // Example position above the bar
                .attr("text-anchor", "middle")
           	 .text(d => d3.format(",")(d.value)) // Format the value with commas
              	 .style("font-size", 18)
                .style("fill", "black");
         
           }
           </script>
           </body>
           </html>

        """.trimIndent()

    }
    fun RLGetNewZoneChartHtml(): String{
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>Stacked Effort Zone Bar Chart</title>
                <script src="https://d3js.org/d3.v6.min.js"></script>
                <style>
                    body { font: 12px Arial; background-color: #FFFFFF; }
                    .axis { font-size:40px }
                    .axis path, .axis line { fill: none; stroke: black; shape-rendering: crispEdges; }
                </style>
            </head>
            <body>
            <div id="chart"></div>
            <script>
            document.addEventListener("DOMContentLoaded", function() {
                var data = [
                    { zone: "Zone1", value: 1829, color: "rgb(241, 119, 160)" },
                    { zone: "Zone2", value: 2345, color: "rgb(255, 207, 47)" },
                    { zone: "Zone3", value: 1230, color: "rgb(44, 174, 44)" },
                    { zone: "Zone4", value: 2310, color: "rgb(0, 153, 218)" },
                    { zone: "Zone5", value: 560, color: "rgb(254, 105, 02)" },
                    { zone: "Zone6", value: 680, color: "rgb(153, 0, 204)" },
                    { zone: "Zone7", value: 120, color: "rgb(237, 69, 65)" }
                ];

                var margin = { top: 20, right: 20, bottom: 40, left: 30 }, // Reduced left margin
                width = window.innerWidth - margin.left - margin.right,
                height = window.innerHeight - margin.top - margin.bottom;

                var svg = d3.select("#chart").append("svg")
                    .attr("width", width + margin.left + margin.right)
                    .attr("height", height + margin.top + margin.bottom)
                    var g = svg.append("g")
                    .attr("transform", "translate(" + (width/2 - 120.0) + "," + margin.top + ")");

                var barWidth = 240; // Set the bar width to 120 pixels

                var y = d3.scaleLinear()
                    .range([height, 0])
                    .domain([0, d3.sum(data, function(d) { return d.value; }) / 60]); // Convert total seconds to minutes

                var x = d3.scaleBand()
                    .range([0, barWidth]) // Set the range for the single bar width
                    .padding(0.1);

                var runningTotal = 0;
                var bar = g.selectAll(".bar")
                    .data(data)
                    .enter().append("rect")
                    .attr("class", "bar")
                    .attr("x", 10) // Set a smaller x value to reduce the gap
                    .attr("width", barWidth)
                    .attr("y", function(d) { var yVal = y(runningTotal + d.value / 60); runningTotal += d.value / 60; return yVal; })
                    .attr("height", function(d) { return height - y(d.value / 60); })        
                    .attr("fill", function(d) { return d.color; });
                    

                    // Add foreignObject
                 const foreignObject = g.append("foreignObject")
                     .attr("width", barWidth+13)
                     .attr("height", height)
                     .style("zIndex", 1)
                     .attr("x", 4)
                     .attr("y", -6);

                 // Add div inside foreignObject
                 const foreignDiv = foreignObject.append("xhtml:div")
                     .style("width", "94%")
                     .style("height", "100%")
                     .style("fill", "none")
                     .style("zIndex", 1)
                     .style("border", "10px solid white")
                     .style("border-top-right-radius", "20px")
                     .style("border-top-left-radius", "20px");

                // Add the Y Axis with exactly 5 ticks
                var yAxis = d3.axisLeft(y).ticks(5).tickFormat(function(d) {
                     return d === 0 ? "Mins" : d; // Replace the 0 with 'Mins'
                });
                g.append("g")
                    .attr("class", "axis")
                    .call(yAxis);
            });
            </script>
            </body>
            </html>

        """.trimIndent()
    }

    fun RLGetNewZoneChartHtml1(jsonArray: JSONArray): String{
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>Stacked Effort Zone Bar Chart</title>
                <script src="https://d3js.org/d3.v6.min.js"></script>
                <style>
                    body { font: 12px Arial; background-color: #FFFFFF; }
                    .axis { font-size:40px }
                    .axis path, .axis line { fill: none; stroke: black; shape-rendering: crispEdges; }
                </style>
            </head>
            <body>
            <div id="chart"></div>
            <script>
            document.addEventListener("DOMContentLoaded", function() {
                var data = $jsonArray

                var margin = { top: 20, right: 20, bottom: 40, left: 30 }, // Reduced left margin
                width = window.innerWidth - margin.left - margin.right,
                height = window.innerHeight - margin.top - margin.bottom;

                var svg = d3.select("#chart").append("svg")
                    .attr("width", width + margin.left + margin.right)
                    .attr("height", height + margin.top + margin.bottom)
                    var g = svg.append("g")
                    .attr("transform", "translate(" + (width/2 - 120.0) + "," + margin.top + ")");

                var barWidth = 240; // Set the bar width to 120 pixels

                var y = d3.scaleLinear()
                    .range([height, 0])
                    .domain([0, d3.sum(data, function(d) { return d.value; }) / 60]); // Convert total seconds to minutes

                var x = d3.scaleBand()
                    .range([0, barWidth]) // Set the range for the single bar width
                    .padding(0.1);

                var runningTotal = 0;
                var bar = g.selectAll(".bar")
                    .data(data)
                    .enter().append("rect")
                    .attr("class", "bar")
                    .attr("x", 10) // Set a smaller x value to reduce the gap
                    .attr("width", barWidth)
                    .attr("y", function(d) { var yVal = y(runningTotal + d.value / 60); runningTotal += d.value / 60; return yVal; })
                    .attr("height", function(d) { return height - y(d.value / 60); })        
                    .attr("fill", function(d) { return d.color; });
                    

                    // Add foreignObject
                 const foreignObject = g.append("foreignObject")
                     .attr("width", barWidth+13)
                     .attr("height", height)
                     .style("zIndex", 1)
                     .attr("x", 4)
                     .attr("y", -6);

                 // Add div inside foreignObject
                 const foreignDiv = foreignObject.append("xhtml:div")
                     .style("width", "94%")
                     .style("height", "100%")
                     .style("fill", "none")
                     .style("zIndex", 1)
                     .style("border", "10px solid white")
                     .style("border-top-right-radius", "20px")
                     .style("border-top-left-radius", "20px");

                // Add the Y Axis with exactly 5 ticks
                var yAxis = d3.axisLeft(y).ticks(5).tickFormat(function(d) {
                     return d === 0 ? "Mins" : d; // Replace the 0 with 'Mins'
                });
                g.append("g")
                    .attr("class", "axis")
                    .call(yAxis);
            });
            </script>
            </body>
            </html>

        """.trimIndent()
    }

    //Challenge Feed Summary Page Chart
    fun RLgetRankingChartHtml(jsondata: JSONArray, userid:String): String{
        return """ 

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Dynamic Bar Chart</title>
    <style>
        body {
            margin: 0;
            padding: 0;
            font-family: Arial, sans-serif;
        }

        .x-axis text {
            font-family: "Omnes";
        }

        .x-axis path, .x-axis line {
            stroke: white;
        }
    </style>
    <script src="https://d3js.org/d3.v7.js"></script>
</head>
<body>
    <script>
        var data = $jsondata;
        var bar_color = "#ebf7ed";
        var percentage_label_color = "#4a4c4f";
        var user_name_label_color = "#4a4c4f"; // Change user_name_label_color to white
       
        var name_label_color = "#4a4c4f";
        var index_label_color = "black";
        var name_font_size = "45px";
        var percentage_font_size = "45px";
        var index_font_size = "50px";
        var font_family = "Omnes, sans-serif";
        var font_vertical_pos = 13;
        //var barHeight = 66; // Fixed bar height
        //var barPadding = 0.25; // Padding between bars



        const barHeight = 66;
        const barPadding = 16; // Fixed padding in pixels
        var user_name = '$userid';
        
        // Find user index and prepare display data
        const userIndex = data.findIndex(d => d.userid === user_name);
        let displayData;

        if (userIndex < 15) {
            displayData = data.slice(0, 15);
        } else {
            displayData = data.slice(0, 14);
            if (!displayData.some(d => d.userid === user_name)) {
                displayData.push(data[userIndex]);
            }
        }

        // Dynamically calculate SVG height
        const svgWidth = window.innerWidth * 0.9;
        const svgHeight = displayData.length * (barHeight + barPadding) + barPadding;


        // Margins and inner dimensions
        const margin = { top: 20, right: 150, bottom: 40, left: 120 };
        const width = svgWidth - margin.left - margin.right;
        const height = svgHeight - margin.top - margin.bottom;

        // Create SVG container
        const svg = d3.select("body")
            .append("svg")
            .attr("width", svgWidth)
            .attr("height", svgHeight)
            .append("g")
            .attr("transform",  `translate(`+ margin.left +`,`+  margin.top +`)`);

        // Define scales
        const xScale = d3.scaleLinear()
            .domain([0, d3.max(data, d => d.value)])
            .range([0, width]);

        const yScale = d3.scaleBand()
            .domain(displayData.map(d => d.name))
            .range([0, svgHeight - barPadding]) // Adjust for fixed padding
            .paddingInner(0) // Set proportional padding to zero for exact gaps
            .paddingOuter(0);



        // Create bars
        svg.selectAll(".bar")
            .data(displayData)
            .enter()
            .append("rect")
            .attr("class", "bar")
            .attr("x", 0)
            .attr("y", d => yScale(d.name))
            .attr("width", d => xScale(d.value))
            .attr("height", barHeight)
            .attr("rx", 12)
            .style("fill", d => d.userid === user_name ? "#39b54a" : "#ebf7ed");

        // Add images as patterns
        svg.append("defs")
            .selectAll("pattern")
            .data(displayData)
            .enter()
            .append("pattern")
            .attr("id", (d, i) => `image-pattern-`+i+``)
            .attr("width", 1)
            .attr("height", 1)
            .attr("patternContentUnits", "objectBoundingBox")
            .append("image")
            .attr("x", 0)
            .attr("y", 0)
            .attr("width", 1)
            .attr("height", 1)
            .attr("preserveAspectRatio", "xMidYMid slice")
            .attr("xlink:href", d => d.image);

        // Add circles with images
        svg.selectAll(".circle")
            .data(displayData)
            .enter()
            .append("circle")
            .attr("cx", -margin.left / 2)
            .attr("cy", d => yScale(d.name) + barHeight / 2)
            .attr("r", barHeight / 2)
            .style("fill", (d, i) => `url(#image-pattern-`+i+`)`);

        // Add labels


        // Draw dashed line if user_name bar is in position 15
        if (userIndex >= 15) {
            const yPos = yScale(displayData[14].name) + (barPadding / 2) - 12;
            //const yPos = yScale(displayData[14].name) + barHeight + barPadding / 2 ;

            svg.append("line")
                .attr("x1", 0)
                .attr("x2", width)
                .attr("y1", yPos)
                .attr("y2", yPos)
                .style("stroke", "black")
                .style("stroke-dasharray", "5,5")
                .style("stroke-width", 2);
        }
       
        // Percentage labels are label4

        svg.selectAll(".label4")
            .data(displayData)
            .enter()
            .append("text")
            .attr("class", "label")
            .attr("x", width + 120) // Adjust the position to right-align the text
            //.attr("y", d => yScale(d.name) + yScale.bandwidth() / 2 + font_vertical_pos + 4)
            .attr("y", d => yScale(d.name) + barHeight / 2 + font_vertical_pos + 4)
            .style("fill", percentage_label_color)
            .style("font-size", percentage_font_size)
            .style("font-family", font_family)
            .style("text-anchor", "end") // Right-align the text
            .text(d => d.value + "%");



        // Find the index of the user in the data array
        var userRowIndex = (data.findIndex(d => d.userid === user_name) + 1);



        // labels in bars
        // Find the first index where the value is 0 to adjust ranking shown for users with value 0
        const firstZeroValueIndex = data.findIndex(d => d.value === 0) + 1; // Adding 1 for 1-based indexing

        // Update the rendering of labels to incorporate conditional logic for rankings

        // Determine the lowest ranking for users with a value of 0 among the first 15 bars
        let ranksWithZeroValue = data
            .map((d, i) => ({ value: d.value, rank: i + 1 }))
            .filter(d => d.value === 0 && d.rank <= 15)
            .map(d => d.rank);

        let lowestRankForZeroValue = Math.min(...ranksWithZeroValue, data.length + 1); // Use a fallback if no 0 values



        // Determine the text to display based on the presence of zero values
        var displayText = data.some(d => d.value === 0) ? firstZeroValueIndex : "User Row Index: " + userRowIndex;


        // Find the highest rank among rows with a value of 0
        let zeroValueRank = data.findIndex(d => d.value === 0) + 1; // First occurrence of 0 value + 1 for rank
        // These are the labels of usernames and ranks in the bars
        
        svg.selectAll(".label1")
            .data(displayData)
            .enter()
            .append("text")
            .attr("class", "label")
            .attr("x", d => 10) // Starting position for the label
            .attr("y", d => yScale(d.name) + barHeight / 2 + font_vertical_pos + 4)
            .style("fill", d => d.userid === user_name ? user_name_label_color : name_label_color)
            .style("font-size", name_font_size)
            .style("font-family", font_family)
            .each(function (d) {
                const textElement = d3.select(this);

                let ranking;
                if (d.value === 0) {
                    ranking = zeroValueRank;
                } else {
                    ranking = data.findIndex(x => x.userid === d.userid) + 1;
                }

                // Extract rank and name from the string
                const rank = ``+ranking+``;
                const name = d.name.split(" - ")[1];

                // Add the name (left-aligned part)
                textElement.append("tspan")
                    .attr("x", 110) // Keep the left alignment for the name
                    .attr("dy", 0) // No vertical adjustment
                    .text(name);

                // Add the rank (right-aligned part)
                textElement.append("tspan")
                    .attr("x", 90) // Position for right alignment
                    .style("text-anchor", "end") // Right-align the rank
                    .text(rank);
            });

        // Helper function to format seconds into hh:mm format
        function formatSecondsToHHMM(seconds) {
            const totalMinutes = Math.floor(seconds / 60);
            const hours = Math.floor(totalMinutes / 60).toString().padStart(2, '0');
            const minutes = (totalMinutes % 60).toString().padStart(2, '0');
            return ``+hours+`h `+minutes+`m`;
        }

        // Add metric values in hh:mm format at the right-hand end of bars
        svg.selectAll(".label3")
            .data(displayData)
            .enter()
            .append("text")
            .attr("class", "label3")
            .attr("x", d => width - 30) // Adjust the position to right-align the text
            .attr("y", d => yScale(d.name) + barHeight / 2 + font_vertical_pos + 4) // Center the text vertically
            .style("fill", d => (d.userid === user_name ? user_name_label_color : name_label_color))
            .style("font-size", name_font_size)
            .style("font-family", font_family)
            .style("text-anchor", "end") // Right-align the text
            .text(d => formatSecondsToHHMM(parseInt(d.number2.replace(/,/g, ''), 10))); // Convert number2 to seconds and format

       
    </script>
</body>
</html>
 """.trimIndent()


    }
    fun RLgetIndividualStepsChartHtml(jsonData: JSONArray): String{
        return """ <!DOCTYPE html>
                        <html>
                        <head>
                          <script src="https://d3js.org/d3.v4.min.js"></script>
                          <style>
                            body {
                              font: 25px sans-serif;
                            }
                            svg {
                              font-family: Sans-Serif, Arial;
                            }
                            .axis path,
                            .axis line {
                              stroke: black;
                            }
                            .bar {
                              fill: #ebf7ed;
                            }
                            .tick line {
                              stroke-opacity: 0.99;
                            }
                          </style>
                        </head>
                        <body>
                        <div id="chart"></div>
                        <script>
                         var array = [
                              {
                                name: "mktest3",
                                values:$jsonData
                              }
                            ];
                        
                          var challengeData = array.find(entry => entry.name === "mktest3");
                        
                          createGraph(challengeData);
                        
                        function createGraph(data) {
                            var margin = { top: 20, right: 50, bottom: 20, left: 120 },
                                barHeight = 66,
                                barSpacing = 16,
                                totalBarsHeight = data.values.length * (barHeight + barSpacing),
                                height = totalBarsHeight + margin.top + margin.bottom,
                                width = window.innerWidth - 50;
                        
                            /* Format Data */
                            var parseDate = d3.timeParse("%Y-%m-%d");
                            data.values.forEach(function(d) {
                                d.date = parseDate(d.date);
                                d.value = +d.value;
                            });
                        
                            /* Scales */
                            var yScale = d3.scaleBand()
                                           .range([margin.top, totalBarsHeight + margin.top])
                                           .paddingInner(barSpacing / (barHeight + barSpacing))
                                           .domain(data.values.map(d => d.date));
                        
                            var xScale = d3.scaleLinear()
                                           .domain([0, d3.max(data.values, d => d.value)])
                                           .range([margin.left, width - margin.right]);
                        
                            var svg = d3.select("#chart").append("svg")
                                        .attr("width", width)
                                        .attr("height", height);
                        
                            /* Add Axes */
                            var yAxis = d3.axisLeft(yScale).tickFormat(d3.timeFormat("%d %b"));
                            var xAxis = d3.axisBottom(xScale).ticks(10);
                        
                            svg.append("g")
                               .attr("transform", "translate(" + margin.left + ",0)")
                               .call(yAxis)
                               .selectAll("text")
                               .style("font-size", 36);
                        
                            svg.append("g")
                               .attr("transform", "translate(0," + (height - margin.bottom) + ")")
                               .call(xAxis)
                               .style("display", "none");
                        
                            /* Draw Bars */
                            svg.selectAll(".bar")
                               .data(data.values)
                               .enter().append("path")
                               .attr("class", "bar")
                               .attr("d", function(d) {
                                   var x = margin.left + 2;
                                   var y = yScale(d.date);
                                   var width = d.value > 0 ? xScale(d.value) - margin.left : 0;
                                   var radius = 10;
                        
                                   if (d.value === 0) {
                                       return "M" + x + "," + y + " h0 v" + barHeight + " h0 z";
                                   }
                        
                                   return "M" + x + "," + y + " " +
                                          "h" + (width - radius) + " " +
                                          "a" + radius + "," + radius + " 0 0 1 " + radius + "," + radius + " " +
                                          "v" + (barHeight - 2 * radius) + " " +
                                          "a" + radius + "," + radius + " 0 0 1 -" + radius + "," + radius + " " +
                                          "h-" + (width - radius) + " " +
                                          "z";
                               })
                               .attr("fill", function(d) { return d.value > 0 ? "#3329f5" : "none"; });
                        
                            /* Add Labels */
                            svg.selectAll(".bar-label")
                               .data(data.values)
                               .enter().append("text")
                               .attr("class", "bar-label")
                               .attr("x", margin.left + 100)
                               .attr("y", function(d) { return yScale(d.date) + barHeight / 2; })
                               .attr("dy", "0.35em")
                               .text(function(d) { return d.value > 0 ? d3.format(",")(d.value) : ""; })
                               .style("font-size", 36)
                               .style("fill", "black");
                        }
                        </script>
                        </body>
                        </html>  
                """.trimIndent()
    }


}