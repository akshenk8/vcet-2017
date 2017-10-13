<?php
	$dbname="bbank";
	$cxn = new mysqli("localhost","root","asd123qwe",$dbname);

	if($cxn->connect_error)
	{
		echo "Connection Error";
		die();
	}

	define ('SITE_ROOT', realpath(dirname(__FILE__)));	
?>
