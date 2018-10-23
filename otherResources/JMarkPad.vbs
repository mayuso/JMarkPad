Set WshShell = CreateObject("WScript.Shell")
WshShell.Run chr(34) & "jre-11\bin\javaw.exe" & Chr(34) & "-jar --add-modules javafx.controls JMarkPad.jar", 0
Set WshShell = Nothing