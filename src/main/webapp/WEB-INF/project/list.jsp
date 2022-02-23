<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!DOCTYPE html>
<html>
<head>
<%@ include file="/common/admin/ace_head.jsp"%>
<title>管理后台</title>
</head>
<body class="no-skin">
	<%@ include file="/common/admin/nav.jsp"%>
	<div class="main-container" id="main-container">
		<jsp:include page="/getMenu.do" flush="true">
			<jsp:param name="menuName" value="user-list" />
		</jsp:include>
		<div class="main-content">
		<!-- 头部标题，用于快速后退 -->
			<div class="breadcrumbs" id="breadcrumbs">
				<ul class="breadcrumb">
					<li><a href="/admin/index.do">首页</a></li>
					<li><a href="#">用户管理</a></li>
					<li class="active">用户列表</li>
				</ul>
			</div>
			<div class="page-content">
				<div class="page-header">
					<h1>
						用户列表<small> <i class="ace-icon fa fa-angle-double-right"></i>
							查询所有注册用户
						</small>
					</h1>
				</div>
				<div class="col-sm-12 gui-datagrid">
					
												<table>	
						<thead>
					        <tr>
					         <th>ID</th>
					          <th>工程名字</th>
					          <th>访问次数</th>
					          <th>索引路径</th>
					          <th>压缩文件</th>
					          <th>英文名</th>
					     

					        </tr>
					    </thead>
					    
					      <tbody>
					          <c:forEach items="${projects }" var="oneUser">
					          <tr>
					          <td>${oneUser.projectId }</td>
					          
					          <td>${oneUser.title }</td>
					          <td>${oneUser.visitCount }</td>
					          <td>${oneUser.indexPath }</td>
					          <td>${oneUser.zipUrl }</td>
					          <td>${oneUser.titleEn }</td>
					          
					        
    
    
					          
					          </tr>
					          </c:forEach>
					          </tbody>
					      <tfoot>
						    <tr>
						      <td>======End Of All========</td>
						    </tr>
						  </tfoot>
					</table>
				
				</div>
			</div>
		</div>
	</div>
	<%@ include file="/common/admin/script.jsp"%>
	<script type="text/javascript" src="/assets/js/ace/elements.fileinput.js"></script>
	<script type="text/javascript" src="/assets/js/upload.js"></script>
	<script type="text/javascript">

	function toMute(userId){
		$.post(baseUrl+'/admin/user/toggleMute.do',{userId:userId},function(res){
			alert(res.msg);
			if(res.success){
				$('.gui-datagrid').gui_datagrid('reload');
			}
		},'json');
	}
	function toFroze(userId){
		$.post(baseUrl+'/admin/user/toggleFroze.do',{userId:userId},function(res){
			alert(res.msg);
			if(res.success){
				$('.gui-datagrid').gui_datagrid('reload');
			}
		},'json');
	}
	$('.gui-datagrid').gui_datagrid({
		onDelete:function(row){
			var idArray='';
			if(row instanceof Array){
				var ids=[];
				for(var i=0;i<row.length;i++){
					ids.push(row.projectId);
				}
				idArray=ids.join(',');
			}else if(row instanceof Object){
				idArray=row.projectId;
			}
			if(idArray!=''){
				$.post(baseUrl+'/admin/project/doDelete.do',{ids:idArray},function(res){
					alert(res.msg);
					if(res.success){
						$('.gui-datagrid').gui_datagrid('reload');
					}
				},'json');
			}else{
				alert('请选择要删除的数据!');
			}
			
		},onEdit:function(row){
			if(row){
				window.location.href='/admin/project/edit.do?pid='+row.projectId;
			}
		}
	});
	</script>
</body>
</html>