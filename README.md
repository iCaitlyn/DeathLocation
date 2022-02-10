# DeathLocation
A version of PlayerDeathLocation by Yupie that has been modified, along with its most noticeable bug fixed, for a server for a private community.

The original project can be seen at: https://www.spigotmc.org/resources/playerdeathlocation.2483/

Due to years of being abandoned, we had taken it upon ourselves to update and maintain it for our own use since 2019, but after finally fixing the config bug, have decided to release the modified source code to share with the wider community.

## Commands
/dl - Displays info about the plugin
/dl reload - Used to reload the plugin's only config file
/dl lookup <name> - Used to lookup a players latest death location
/dl check - Used to display the player's own last death location
/dl return [safe] - Used to return to the player's death point. Using the "safe" option returns the player to a point higher from where they died, so they can float down safely to it, if they died by suffocation.
  
## Permissions
dl.use
  description: Used to send a message to the player with the death location
  default: OP
dl.return
  description: Allows the player to use the "/dl return" command
dl.log
  description: Used to log the players latest death location
  default: OP
dl.lookup
  description: Allows the use of the "/dl lookup" command
  default: OP
dl.reload
  description: Allows the use of the "/dl reload" command
  default: OP
  
## Notes
Added rudimentary "support" for EssentialsX based on permission detection, to change the message they get with and without the Essentials /back (on death) command
