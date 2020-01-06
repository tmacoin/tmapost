<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
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
    
	    <h2>Display Post</h2>
	    
	    <div class="row"> 
	      <div class="col-md-2 font-weight-bold">Name:</div>
	      <div class="col-md-10 text-truncate">${post.name}</div>
	    </div>
	    <div class="row"> 
	      <div class="col-md-2 font-weight-bold">Description:</div>
	      <div class="col-md-10" style="white-space: pre-wrap; word-wrap: break-word" >${post.description}</div>
	    </div>
	    <div class="row"> 
	      <div class="col-md-2 font-weight-bold">Date:</div>
	      <div class="col-md-10">
	      	<jsp:useBean id="startDate" class="java.util.Date" />
			<jsp:setProperty name="startDate" property="time" value="${post.timeStamp}"/>
	        <fmt:formatDate value="${startDate}" type="date" dateStyle="long"/>
	      </div>
	    </div>
	    <div class="row"> 
	      <div class="col-md-2 font-weight-bold">Identifier:</div>
	      <div class="col-md-10 text-truncate">${post.transactionId}</div>
	    </div>
	    <div class="row"> 
	      <div class="col-md-2 font-weight-bold">Rating:</div>
	      <div class="col-md-10">${post.totalRating}</div>
	    </div>
	    <div class="row"> 
	      <div class="col-md-2 font-weight-bold">Creator Address:</div>
	      <div class="col-md-10 text-truncate">${post.creatorTmaAddress}</div>
	    </div>
	    
	    <s:form action="add-rating">
	      <input type="hidden" name="transactionId" value="${post.transactionId}"/>
	      <input type="hidden" name="name" value="${post.name}"/>
		  <div class="form-group row">
		    <label for="comment" class="col-sm-2 col-form-label font-weight-bold">Comment:</label>
		    <div class="col-sm-10">
		      <textarea class="form-control" rows="5" id="comment" name="comment" placeholder="Comment"></textarea>
		    </div>
		  </div>
		  
		  <div class="form-group row">
		    <label for="rating" class="col-sm-2 col-form-label font-weight-bold">Rating:</label>
				<div class="col-sm-10">
					<fieldset class="border p-2 rounded">
						<legend class="w-auto">Approve?</legend>
						<label class="radio-inline"><input type="radio" name="rate" value="Yes">Yes</label> 
						<label class="radio-inline"><input type="radio" name="rate" value="No">No</label>
					</fieldset>
				</div>
			</div>
	
		  <div class="form-group row">
		    <div class="col-sm-10">
		      <button type="submit" class="btn btn-dark">Add Rating</button>
		    </div>
		  </div>
		</s:form>
		
		<h2>Comments</h2>
		
	  	<s:iterator value="ratings" status="rating">
	  		<hr/>
		  	<div class="row"> 
		      <div class="col-md-2 font-weight-bold">Rater:</div>
		      <div class="col-md-10 text-truncate">
		      	<a href="${pageContext.request.contextPath}/show-ratings?rater=${rater}">${rater}</a>
		      </div>
		    </div>
			<div class="row"> 
		      <div class="col-md-2 font-weight-bold">Post:</div>
		      <div class="col-md-10 text-truncate">${ratee}</div>
		    </div>
			<div class="row"> 
		      <div class="col-md-2 font-weight-bold">Rate:</div>
		      <div class="col-md-10 text-truncate">
		      	${rate == 'Yes' ? 'Positive': 'Negative'}
		      </div>
		    </div>
			<div class="row"> 
		      <div class="col-md-2 font-weight-bold">Date:</div>
		      <div class="col-md-10 text-truncate">
		      	<jsp:setProperty name="startDate" property="time" value="${timeStamp}"/>
		        <fmt:formatDate value="${startDate}" type="date" dateStyle="long"/>
		      </div>
		    </div>
			<div class="row"> 
		      <div class="col-md-2 font-weight-bold">Comment:</div>
		      <div class="col-md-10" style="white-space: pre-wrap; word-wrap: break-word">${comment}</div>
		    </div>	    
				    
		</s:iterator>


    
    </div>
    

    <!-- Optional JavaScript -->
    <!-- jQuery first, then Popper.js, then Bootstrap JS -->
    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>
  </body>
</html>