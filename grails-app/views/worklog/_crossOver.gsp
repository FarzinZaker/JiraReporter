<%@ page import="java.text.SimpleDateFormat; grails.converters.JSON" %>
<div id="crossOver"></div>
<script language="JavaScript" type="text/javascript">
    Highcharts.chart('crossOver', {

        chart: {
            type: 'heatmap',
            marginTop: 40,
            marginBottom: 80,
            plotBorderWidth: 1
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
                [0, '#DD2C00'],
                [0.5, '#ffffff'],
                [0.9, '#00C853']
            ]
        },

        legend: {
            align: 'right',
            layout: 'vertical',
            margin: 0,
            verticalAlign: 'top',
            y: 25,
            symbolHeight: 280
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
            data: [
                <g:each in="${summary.developers}" var="developer" status="i">
                <g:each in="${summary.dates.sort()}" var="date" status="j">
                {
                    x:${i},
                    y:${j},
                    value:${summary.data[developer][date]['differencePercent']},
                    difference: ${summary.data[developer][date]['difference']},
                    jira: '${summary.data[developer][date]['jira']?:'-'}',
                    crossOver: '${summary.data[developer][date]['crossOver']?:'-'}'
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
</script>