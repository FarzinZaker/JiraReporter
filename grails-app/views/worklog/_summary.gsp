<h2>Time Spent per ${label}</h2>

<div id="summary_${label?.replace(' ', '_')}"></div>

<script language="JavaScript" type="text/javascript">
    function timeFormat(time) {
        var secs = time * 3600;
        var mins = 0;
        var hours = 0;
        var days = 0;

        mins = ((secs - (secs % 60)) / 60);
        secs = (secs % 60);

        hours = ((mins - (mins % 60)) / 60);
        mins = (mins % 60);

        days = ((hours - (hours % 8)) / 8);
        hours = (hours % 8);

        var timeSpent = '';
        if (days > 0) {
            if (timeSpent != '')
                timeSpent += ' ';
            timeSpent += days + 'd';
        }
        if (hours > 0) {
            if (timeSpent != '')
                timeSpent += ' ';
            timeSpent += hours + 'h';
        }
        if (mins > 0) {
            if (timeSpent != '')
                timeSpent += ' ';
            timeSpent += mins + 'm';
        }

        return timeSpent;
    }

    Highcharts.chart('summary_${label?.replace(' ', '_')}', {
        chart: {
            type: 'column'
        },
        title: false,
        xAxis: {
            categories: [
                <g:each in="${items}" var="item">
                '${item.key}',
                </g:each>
            ],
            crosshair: true
        },
        yAxis: {
            min: 0,
            title: {
                text: 'Time Spent (hours)'
            }
        },
        legend: false,
        tooltip: {
            formatter: function (tooltip) {
                return '<div style="font-size:10px">' + this.point.category + '</div><b>' + timeFormat(this.point.y) + '</b>';
            },
            useHTML: true
        },
        plotOptions: {
            column: {
                pointPadding: 0.2,
                borderWidth: 0
            },
            series: {
                dataLabels: {
                    enabled: true,
                    formatter:function()
                    {
                        return timeFormat(this.y);
                    }
                }
            }
        },
        series: [{
            name: 'Time Spent',
            data: [
                <g:each in="${items}" var="item">
                ${item.value / 3600},
                </g:each>
            ],
            color: '${color}'
        }]
    });
</script>