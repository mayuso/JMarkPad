Set WshShell = CreateObject("WScript.Shell")
WshShell.Run chr(34) & "jre-11\bin\javaw.exe" & Chr(34) & "-jar JMarkPad.jar", 0
Set WshShell = Nothing