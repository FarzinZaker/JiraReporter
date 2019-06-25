<g:set var="users" value="${data.keySet()}"/>
<g:set var="others" value="${new HashSet()}"/>
<g:each in="${data}" var="user">
    <g:each in="${user.value.others}" var="other">
        <g:set var="others" value="${others + other.key}"/>
    </g:each>
</g:each>
<h2>Time Spent per ${label}</h2>
<table class="pivot">
    <thead>
    <tr>
        <td>
            ${label}
        </td>
        <g:each in="${users}" var="user">
            <td class="user center">
                <g:render template="user" model="${[user: data[user].data]}"/>
            </td>
        </g:each>
    </tr>
    </thead>
    <tbody>
    <g:each in="${others}" var="other">
        <tr>
            <td class="other">
                ${other}
            </td>
            <g:each in="${users}" var="user">
                <td class="timeSpent center">
                    ${data[user].others[other]?.timeSpent ?: '-'}
                </td>
            </g:each>
        </tr>
    </g:each>
    </tbody>
</table>