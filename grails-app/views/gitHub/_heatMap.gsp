<%@ page import="java.text.SimpleDateFormat; grails.converters.JSON" %>
<g:if test="${heatMap?.size()}">
    <div id="heatMap"></div>
    <script language="JavaScript" type="text/javascript">
        <g:set var="chartHeight" value="${languages?.size() * 60 + 50}"/>
        <g:if test="${chartHeight < 350}">
        <g:set var="chartHeight" value="${350}"/>
        </g:if>
        $(document).ready(function () {
            Highcharts.chart('heatMap', {

                chart: {
                    type: 'heatmap',
                    marginTop: 120,
                    marginRight: 130,
                    marginLight: -50,
                    plotBorderWidth: 1,
                    height: ${chartHeight},
                },


                title: {
                    text: ''
                },

                xAxis: {
                    categories: [
                        <g:each in="${heatMap}" var="company">
                        <g:each in="${company.value}" var="product">
                        '<format:html value='${company.key}: ${product.key}'/>',
                        </g:each>
                        </g:each>
                    ],
                    opposite: true
                },

                yAxis: {
                    categories: [
                        <g:each in="${languages.sort{it.value}}" var="language">
                        '${language.key}',
                        </g:each>
                    ],
                    labels: {
                        rotation: -45
                    },
                    title: null,
                    opposite: true
                },

                colorAxis: {
                    min: 1,
                    max: ${languagesMax.collect{it.value}.max()},
                    stops: [
                        [0, '#FFFFFF'],
                        [0.2, '#FFEB3B'],
                        [0.4, '#FFB300'],
                        [0.6, '#FF9800'],
                        [0.8, '#FF5722'],
                        [1.0, '#B71C1C']
                    ],
                    type:'logarithmic'
                },

                legend: {
                    align: 'right',
                    layout: 'vertical',
                    margin: 50,
                    verticalAlign: 'top',
                    y: 100,
                    // symbolHeight: 280
                },

                tooltip: {
                    formatter: function () {
                        if(this.point.value === 1)
                            return false;
                        return '<b>' + this.series.xAxis.categories[this.point.x] + '</b><br/>' +
                            this.series.yAxis.categories[this.point.y] + '<br/><hr/>' +
                            'LOC: <b>' + formatNumber(this.point.value) + '</b><br/>';
                    },
                    useHTML: true
                },

                series: [{
                    name: 'Lines of Code',
                    borderWidth: 1,
                    turboThreshold: 10000000,
                    data: [
                        <g:set var="i" value="${0}"/>
                        <g:each in="${heatMap.keySet()}" var="company">
                        <g:each in="${heatMap[company].keySet()}" var="product">
                        <g:each in="${languages.sort{it.value}}" var="language" status="j">
                        {
                            x:${i},
                            y:${j},
                            value:${heatMap[company][product][language.key]},
                            %{--difference: ${summary.data[developer][date]['difference'] != null ? summary.data[developer][date]['difference'] : 0},--}%
                            %{--jira: '${summary.data[developer][date]['jira'] ?: '-'}',--}%
                            %{--crossOver: '${summary.data[developer][date]['crossOver'] ?: '-'}'--}%
                        },
                        </g:each>
                        <g:set var="i" value="${i+1}"/>
                        </g:each>
                        </g:each>
                    ],
                    dataLabels: {
                        enabled: true,
                        color: '#000000',
                        formatter: function () {
                            return formatBigNumber(this.point.value);
                        }
                    }
                }
                ]

            })
            ;
        });

        function formatNumber(num) {
            if(num === 1)
                return '-';
            return num.toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,');
        }

        function formatBigNumber(num) {
            if(num === 1)
                return '-';
            var postfix = '';
            if (num > 1000000) {
                num = num / 1000000;
                postfix = 'M'
            } else if (num > 1000) {
                num = num / 1000;
                postfix = 'K'
            }
            num = Math.round(num);
            return num.toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1,') + postfix;
        }
    </script>
</g:if>
<g:else>
    <div class="info" style="margin-top:20px;">
        No records to display
    </div>
</g:else>