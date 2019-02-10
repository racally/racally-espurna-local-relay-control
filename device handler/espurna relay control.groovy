/**
 *  Espurna local Relay V2
 *  Original Author     : racally@gmail..com
 *  Creation Date       : 2019-02-03
 *
 *  
 *  Disclaimer about 3rd party server: No longer uses third-party server :)
 * 
 *  Changelog:
 *
 */
 
import groovy.json.JsonSlurper

preferences {
    input "ip", "string", title: "IP Address", required: true
    input "port", "number", title: "Port", required: true
    input "cmd", "text", title: "cmd", description: "The api cmd sent ", required: true
    input "apiKey", "text", title: "apiKey", description: "The device apiKey (found on portal)", required: true
} 
 
metadata {
    definition (name: "racally: Espurna local relay", author: "r.callaghan", oauth: false) {
        capability "Polling"
        capability "Switch"
        capability "Refresh"
    }

    // simulator metadata
    simulator {
    }

    // UI tile definitions
    tiles(scale:2) {
       multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
            tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'${name}', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#79b821", nextState:"turningOff"
                attributeState "off", label:'${name}', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"turningOn"
                attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#79b821", nextState:"turningOff"
                attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"turningOn"
            }           
        }
        
        standardTile("refresh", "device.switch", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
            state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
        }

        main(["switch"])
        details(["switch", "refresh"])
    }
}

def initialize()
{
    poll()
}

def poll()
{
  	log.debug "Polling.."
  	getStatus()
  	runIn(60, refresh)
}

def installed() {
	log.debug "installed()"
	configure()
}

def configure() {
	log.debug "configure()"
    configure()
}

def updated() {
	log.debug "updated()" 
	configure();
}

def refresh() {
	log.debug "Refreshing.."
    poll()
}


def ping()
{
    log.debug "Pinging.."
    poll()
}


def on() 
{
    log.debug "Turning device ON"
    sendCmd("1")
    sendEvent(name: "switch", value: "on");
}

def off() 
{
    log.debug "Turning device OFF"
    sendCmd("0")
    sendEvent(name: "switch", value: "off");
}

def sendCmd(num)
{
   	log.debug "Sending Command"
	sendHubCommand(new physicalgraph.device.HubAction("""GET ${settings.cmd}0?apikey=${settings.apiKey}&value=${num} HTTP/1.1\r\nHOST:${settings.ip}:${settings.port}\r\nAuthorization: Basic B64STRING\r\n\r\n""", 
    physicalgraph.device.Protocol.LAN, "${settings.ip}")) 
}

def getStatus() {
	log.debug "Refreshing.."
	def hubAction2 = new physicalgraph.device.HubAction(
	method: "GET",
	path: "/api/relay/0?apikey=84C8700AA431A91D&value=?",
	headers: [
   	host: "${settings.ip}:${settings.port}"
	]
  	) 
sendHubCommand(hubAction2)
  	return hubAction2
}

def parse(description) {
	def descMap = parseDescriptionAsMap(description)
	def body
	
if (descMap["body"]) body = new String(descMap["body"].decodeBase64())
	
if (body && body != "") {
	log.debug "Device Status: $body"
	String res = "${body}"
        
        switch (res) {                                  
		case "0":
        sendEvent(name: "switch", value: "off")
		log.debug "Device is off..."             
		break;           
		case "1":   
        sendEvent(name: "switch", value: "on")
		log.debug "Device is on..."             
		break; 
        default:
        sendEvent(name: "switch", value: "offline")
        log.debug buffer
        log.debug "Device status unknown"  
        break; 
        }        
}
}

def parseDescriptionAsMap(description) {
	description.split(",").inject([:]) { map, param ->
		def nameAndValue = param.split(":")
    
    if (nameAndValue.length == 2) map += [(nameAndValue[0].trim()):nameAndValue[1].trim()]
    else map += [(nameAndValue[0].trim()):""]
	}
}