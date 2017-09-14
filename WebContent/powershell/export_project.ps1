#powershell真的很强大，但是我真的不太会
param($file_path,$file_name)
$project=new-object -comobject msproject.application
$file_path_name= ($file_path+$file_name+".mpx")-join ""
$project.FileOpen($file_path_name)
$project.FileSaveAs(($file_path+$file_name+".mpp")-join "")
$project.Quit()
Write-Host 'export_project_over'
