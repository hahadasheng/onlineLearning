<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Hello World!</title>
</head>
<body>
Hello ${name}!

<hr><br> -- list指令 <br><hr>

<table>
    <tr>
        <td>序号</td>
        <td>姓名</td>
        <td>年龄</td>
        <td>钱包</td>
    </tr>
    <#list stus as stu>
        <tr>
            <td>${stu_index + 1}索引</td>
            <td>${stu.name}</td>
            <td>${stu.age}</td>
            <td>${stu.money}</td>
        </tr>
    </#list>
</table>

<hr><br> -- 读取map数据 <br><hr>

输出stu1的学生信息：<br>
姓名：${stuMap["stu1"].name} &nbsp; 年龄：${stuMap["stu1"].age} <br>
姓名：${stuMap.stu2.name} &nbsp; 年龄：${stuMap.stu2.age} <br>

<hr><br> -- list + map 遍历map的key & if指令 <br><hr>
<#list stuMap?keys as key>
    ${key_index + 1} &nbsp;
    ${stuMap[key].name} &nbsp;
    ${stuMap[key].age} &nbsp;
    ${stuMap[key].money} &nbsp;
    <#if stuMap[key].money gt 200>rich man</#if><br>
</#list>

<hr><br> -- null 空值处理 <br><hr>

<#if stuMap??>
    stuMap 变量存在
</#if> <br>
<#if testNull??>
    我不会被显示
</#if> <br>

<hr><br> -- 变量缺失，使用默认值 <br><hr>

${testNull!"变量缺失测试1"} <br>
${(testNull.name)!"变量缺失测试2"} <br>

<hr><br> -- 内建函数 <br><hr>

集合大小：${stus?size} <br>

日期格式化 <br>
${today?date} <br>
${today?time} <br>
${today?datetime} <br>
${today?string("YYYY年MM月dd日 HH:ss:mm")} <br>

内建函数C，不按照默认方式显示数字,类似于 123,456 <br>
${point?c} <br>

assign标签， 变量 <br>

<#assign wife = "{'name':'晓庆','age':18}"/>
<#assign w = wife?eval />

姓名：${w.name} 年龄：${w.age}

</body>
</html>