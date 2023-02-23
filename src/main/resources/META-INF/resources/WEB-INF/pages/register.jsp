<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>org.ua</title>
</head>
<body>
<div align="center">
    <c:url value="/newuser" var="regUrl"/>

    <form action="${regUrl}" method="POST" enctype="multipart/form-data">
        Login:<br/><input type="text" name="login" value="${login}"><br/>
        Password:<br/><input type="password" name="password"><br/>
        E-mail:<br/><input type="text" name="email"><br/>
        Phone:<br/><input type="text" name="phone"><br/>
        Address:<br/><input type="text" name="address"><br/>
        Photo:<br/><input type="file" name="photo" accept="image/*"><br/>
        <input type="submit"/>


        <c:if test="${exists ne null}">
            <p>User already exists!</p>
        </c:if>

        <c:if test="${password == true}">
            <p>Password should be greater than 8 symbols!</p>
        </c:if>
    </form>
</div>
</body>
</html>