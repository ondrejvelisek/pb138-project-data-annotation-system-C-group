<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" trimDirectiveWhitespaces="true" session="false" %>

<!--
@author Matej Rajtár <matej.rajtar@gmail.com>
-->

<!DOCTYPE html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"
          integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous">
    <link type="text/css" rel="stylesheet" href="<c:url value="/resources/css/style.css" />"/>
    <title>Annotation system</title>
</head>
<body>
    <div class="container">

        <div id="finish">
            <form action="<spring:url value="/packages"/>">
                <input type="submit" class="btn btn-lg btn-primary btn-block" value="Finish marking and return">
            </form>
        </div>

        <div class="column-in">
            <div id="counter">1/1000</div>
            <div>
                <span id="fileid">Subpack id: ${thisSubpack.id}</span>
                </br>
                <span id="filename">Subpack name: ${thisSubpack.name}</span>
            </div>
            <div id="ontology">Is the following word an ${thisQuestion}?</div>
            <div id="np">${thisAnswer.answer}</div>
            </br>
            <div id="wrap">
                <form id="answerForm" action="/mark/${thisSubpack.id}/${thisAnswer.id}/report" method="POST" enctype="multipart/form-data">
                    <input type="button" value="Previous word" name="btnPrevious" />
                    <input id="value" name="value" type="submit" value="Report this word" name="btnChangeContext" />
                </form>

            </div>
        </div>

        <div id="left">
            <div class="column-in">
                <div class="next" id="minus-next">
                    <form id="answerForm" action="/mark/${thisSubpack.id}/${thisAnswer.id}/${lStartTime}" method="POST" enctype="multipart/form-data">
                        <input id="value" name="value" type="image" alt="no" value="0" src="<c:url value="/resources/images/no.svg" />" width="100%"/>
                    </form>
                </div>
            </div>
        </div>

        <div id="right">
            <div class="column-in">
                <div class="next" id="plus-next">
                    <form id="answerForm" action="/mark/${thisSubpack.id}/${thisAnswer.id}/${lStartTime}" method="POST" enctype="multipart/form-data">
                        <input id="value" name="value" type="image" alt="no" value="1" src="<c:url value="/resources/images/yes.svg" />" width="100%"/>
                    </form>
                </div>
            </div>
        </div>

    </div>
</body>
</html>
