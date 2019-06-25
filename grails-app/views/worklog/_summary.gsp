<table class="list">
    <thead>
        <tr>
            <td>Engineer</td>
            <td>Time Spent</td>
        </tr>
    </thead>
    <tbody>
    <g:each in="${summary?.sort { -it.value.timeSpendSeconds }}" var="item">
        <tr>
            <td class="user">
                <g:render template="user" model="${[user: item.value.data]}"/>
            </td>
            <td class="timeSpent">
                ${item.value.timeSpent}
            </td>
        </tr>
    </g:each>
    </tbody>
</table>