# racally-espurna-local-relay-control
Basic Smartthings device handler to control devices flashed with espurna firmware using HTTP API

This is first attempt creating a device handler and first use of this scripting code (groovy)

Please ensure you have HTTP API active on your device (log into the device > Admin > enable this)
Please ensure you have Restful API deactive on you device (log into the device > Admin disable this)
BE AWARE THERE IS AN OPEN DEFECT WITH ESPURNA WHICH WILL ENABLE RESTFUL API ON RESTART - if this setting is enabled then the device handler will NOT work.


This handler will allow you to control a device locally on the network while the above settings are correct. Currently you can only address one relay (relay 0) Refresh the device status on requst or allow smartthings to poll the status of the device.
Polling is set to 120 secounds.

