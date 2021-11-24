javac -g -Xlint -Xdiags:verbose  -cp .;renderer_2.jar  %1
java  -Dsun.java2d.uiScale=1.0   -cp .;renderer_2.jar  %~n1
pause
