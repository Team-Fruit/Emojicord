cd /d %~dp0
call gradlew setupDevWorkspace setupDecompWorkspace eclipse genEclipseRuns
pause