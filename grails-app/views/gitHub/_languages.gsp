<g:if test="${languages.size()}">
    <div id="languages"></div>

    <g:set var="chartHeight" value="${languages?.size() * 50 + 50}"/>
    <g:if test="${chartHeight < 350}">
        <g:set var="chartHeight" value="${350}"/>
    </g:if>
    <script language="JavaScript" type="text/javascript">

        $(document).ready(function () {
            Highcharts.chart('languages', {
                chart: {
                    type: 'bar',
                    height: ${chartHeight}
                },
                title: false,
                xAxis: {
                    categories: [
                        <g:each in="${languages.sort{-it.value['loc']}}" var="item">
                        '${item.key}',
                        </g:each>
                    ],
                    crosshair: true,
                    opposite: true
                },
                yAxis: [{
                    min: 0,
                    title: {
                        text: 'Lines of Code'
                    },
                    opposite: true,
                    reversed: true
                }, {
                    min: 0,
                    title: {
                        text: 'Repositories'
                    },
                    opposite: true,
                    reversed: true
                }],
                legend: false,
                tooltip: {
                    formatter: function (tooltip) {
                        return '<div style="font-size:10px">' + this.point.category + '</div>' + this.series.name + ': <b>' + formatNumber(this.point.y) + '</b>';
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
                            formatter: function () {
                                return formatNumber(this.y);
                            }
                        }
                    }
                },
                series: [{
                    name: 'LOC',
                    data: [
                        <g:each in="${languages.sort{-it.value['loc']}}" var="item">
                        ${item.value['loc']},
                        </g:each>
                    ],
                    color: '#3F51B5',
                    dataLabels: {
                        enabled: true,
                        color: '#000000',
                        formatter: function () {
                            return formatBigNumber(this.y);
                        }
                    }
                }, {
                    name: 'Count',
                    data: [
                        <g:each in="${languages.sort{-it.value['loc']}}" var="item">
                        ${item.value['count']},
                        </g:each>
                    ],
                    color: '#009688',
                    dataLabels: {
                        enabled: true,
                        color: '#000000',
                        formatter: function () {
                            return formatNumber(this.y);
                        }
                    },
                    yAxis: 1
                }]
            });
        });
    </script>
</g:if>
<g:else>
    <div class="info" style="margin-top:20px;">
        No records to display
    </div>
</g:else>