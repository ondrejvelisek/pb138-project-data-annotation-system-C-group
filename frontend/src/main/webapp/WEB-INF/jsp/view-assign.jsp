<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" trimDirectiveWhitespaces="true" session="false" %>


<!DOCTYPE html>
<html lang="${pageContext.request.locale}">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous">
    <title>Annotation system</title>
</head>
<body>

<div class="container">
    <h2>Assign users to package ${pack.name}</h2>
    <form id="fileAssignForm" method="POST" enctype="multipart/form-data">
    <div id="middle">
        <ul class="list-group">
            <c:forEach items="${users}" var="person">
                <li class="list-group-item">
                    <a href="<spring:url value="/assign/${pack.id}/${person.id}"/>" type="submit" class="btn btn-link">${person.username}</a>
                </li>
            </c:forEach>
            <li class="list-group-item">
                <a href="<spring:url value="/assign/3/1"/>" type="submit" class="btn btn-link">User1 </a>
            </li>
        </ul>
    </div>
    </form>
    <form action="<spring:url value="/"/>">
        <input type="submit" class="btn btn-lg btn-primary btn-block" value="Back to main menu">
    </form>
</div>
</body>
</html>
