<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!doctype html>
<html lang="en">
  <head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">

    <title>TMA Post</title>
  </head>
  <body>
  
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
      <a class="navbar-brand" href="${pageContext.request.contextPath}/">
	    <img src="${pageContext.request.contextPath}/media/tma.svg" width="30" height="30" alt="">
	  </a>
	  <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
	    <span class="navbar-toggler-icon"></span>
	  </button>
	
	  <div class="collapse navbar-collapse" id="navbarSupportedContent">
	    <ul class="navbar-nav mr-auto">
	      <li class="nav-item">
	        <a class="nav-link" href="${pageContext.request.contextPath}/">Home <span class="sr-only">(current)</span></a>
	      </li>
	      <li class="nav-item">
	        <a class="nav-link" href="${pageContext.request.contextPath}/balance">Get Balance</a>
	      </li>
	      <li class="nav-item dropdown active">
	        <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
	          Posting
	        </a>
	        <div class="dropdown-menu bg-dark" aria-labelledby="navbarDropdown">
	          <a class="dropdown-item bg-dark text-white-50" href="${pageContext.request.contextPath}/post">Create Post</a>
	          <a class="dropdown-item bg-dark text-white-50" href="${pageContext.request.contextPath}/posts">Find Post</a>
	        </div>
	      </li>
	    </ul>
	  </div>
	</nav>
  
    <div class="container">
    
    <s:if test="hasActionMessages()">
    	<div class="alert alert-dark" role="alert">
		  <s:actionmessage/>
		</div>
	</s:if>
	
	<h2>Found Posts</h2>
    
    <div class="table-responsive">
	<table class="table table-hover table-dark">
		  <thead>
		    <tr>
		      <th scope="col">Name</th>
		      <th scope="col">Date</th>
		      <th scope="col">Rating</th>
		      <th scope="col">Creator tma address</th>
		    </tr>
		  </thead>
		  <tbody>
		  	<s:iterator value="posts" status="post">
		    <tr>
		      <td>
		      	<s:url action="show-post" var="url">
		      		<s:param name="identifier">${transactionId}</s:param>
		      		<s:param name="name">${name}</s:param>
		      	</s:url>
		      	
		      	<a href="${url}">${name}</a>
		      </td>
		      <td>
		      	<jsp:useBean id="startDate" class="java.util.Date" />
			  	<jsp:setProperty name="startDate" property="time" value="${timeStamp}"/>
              	<fmt:formatDate value="${startDate}" type="date" dateStyle="long"/>
		      </td>
		      <td>${totalRating}</td>
		      <td>${creatorTmaAddress}</td>
		    </tr>
		    </s:iterator>
		  </tbody>
	</table>
	</div>
	
    </div>
    

    <!-- Optional JavaScript -->
    <!-- jQuery first, then Popper.js, then Bootstrap JS -->
    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>
  </body>
</html>