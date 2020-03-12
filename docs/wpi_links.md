# WPI Links


### VS Code links:
* Creating test program: https://docs.wpilib.org/en/latest/docs/getting-started/running-a-benchtop/creating-benchtop-test-program-cpp-java.html
* Deploying: https://docs.wpilib.org/en/latest/docs/software/wpilib-overview/deploying-robot-code.html#building-and-deploying-robot-code

### Misc
* IP Addresses/Network Stuff: https://docs.wpilib.org/en/latest/docs/networking/networking-introduction/roborio-network-troubleshooting.html
  * `roboRIO-1444-FRC.local`, `10.14.44.2`, `172.22.11.2`
### Networking on Linux
```shell script
sudo vi /etc/network/interfaces
```
add this to bottom:
```
iface eno1 inet static
address 10.14.44.<NUMBER BETWEEN 6 AND 9 I GUESS>
netmask 255.255.255.0
gateway 10.14.44.1 # may not actually need this, I accidentally put 10.44.44.1 and it still worked
```
now run this:
```shell script
sudo ifdown eno1 && sudo ifup eno1
```
NOTE: `eno1` should be replaced with whatever your ethernet thing is

### CTRE Links:
TODO
