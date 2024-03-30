<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Результаты поиска</title>
</head>
<body>

<div id="searchContainer" style="text-align:center; margin-top: 0%;">
    <h1>Новый запрос</h1>
    <form action="search" method="get">
        <input type="text" name="query" placeholder="Введите запрос" required>
        <button type="submit">Поиск</button>
    </form>
</div>

<h1>Результаты поиска</h1>
<#if results?? && results?size gt 0>
    <p>Найдены следующие совпадения:</p>
    <ol>
        <#list results as result>
            <li><a href="${result}" target="_blank"> ${result}</a></li>
        </#list>
    </ol>
<#elseif results?? && results?size == 0>
    <p>По вашему запросу ничего не найдено.</p>
<#else>
    <p>Введите поисковый запрос в форму поиска.</p>
</#if>
</body>
</html>