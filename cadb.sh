MYIP=192.168.43.1:5555

key="$1"

if [ "$key" == -r ]; then
  adb tcpip 5555
elif [ "$key" == -d ]; then
  adb disconnect $MYIP
elif [ "$key" == -i ]; then
  adb shell ip -l a
else
  adb connect $MYIP
fi
