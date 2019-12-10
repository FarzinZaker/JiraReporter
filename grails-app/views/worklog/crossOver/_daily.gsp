<%@ page import="java.text.SimpleDateFormat; grails.converters.JSON" %>
<h2>${label} Report</h2>

<div id="dailySummary"></div>
<script language="JavaScript" type="text/javascript">
    <g:set var="chartHeight" value="${summary.dates?.size() * 60 + 50}"/>
    <g:if test="${chartHeight < 350}">
    <g:set var="chartHeight" value="${350}"/>
    </g:if>
    $(document).ready(function () {
        Highcharts.chart('dailySummary', {

            chart: {
                type: 'heatmap',
                marginTop: 40,
                marginBottom: 80,
                plotBorderWidth: 1,
                height: ${chartHeight}
            },


            title: {
                text: ''
            },

            xAxis: {
                categories: [
                    <g:each in="${summary.developers}" var="developer">
                    '${developer}',
                    </g:each>
                ]
            },

            yAxis: {
                categories: [
                    <g:each in="${summary.dates.sort()}" var="date">
                    '${new SimpleDateFormat('EEE, MMM dd, yyyy').format(date)}',
                    </g:each>
                ],
                title: null
            },

            colorAxis: {
                min: -100,
                max: 100,
                stops: [
                    [0, '#E91E63'],
                    [0.5, '#ffffff'],
                    [0.9, '#009688']
                ]
            },

            legend: {
                align: 'right',
                layout: 'vertical',
                margin: 0,
                verticalAlign: 'top',
                y: 25,
                // symbolHeight: 280
            },

            tooltip: {
                formatter: function () {
                    return '<b>' + this.series.xAxis.categories[this.point.x] + '</b><br/>' +
                        this.series.yAxis.categories[this.point.y] + '<br/><hr/>' +
                        'Jira: <b>' + this.point.jira + '</b><br/>' +
                        'CrossOver: <b>' + this.point.crossOver + '</b>';
                },
                useHTML: true
            },

            series: [{
                name: 'Sales per employee',
                borderWidth: 1,
                turboThreshold: 10000000,
                data: [
                    <g:each in="${summary.developers}" var="developer" status="i">
                    <g:each in="${summary.dates.sort()}" var="date" status="j">
                    {
                        x:${i},
                        y:${j},
                        value:${summary.data[developer][date]['differencePercent'] != null ? summary.data[developer][date]['differencePercent'] : 0},
                        difference: ${summary.data[developer][date]['difference'] != null ? summary.data[developer][date]['difference'] : 0},
                        jira: '${summary.data[developer][date]['jira'] ?: '-'}',
                        crossOver: '${summary.data[developer][date]['crossOver'] ?: '-'}'
                    },
                    </g:each>
                    </g:each>
                ],
                dataLabels: {
                    enabled: true,
                    color: '#000000'
                }
            }]

        });
    });
</script>