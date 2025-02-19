/**
 *  Konnected Beep/Blink
 *
 *  Copyright 2017 konnected.io
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */

metadata {
  definition (name: "Konnected Beep/Blink", namespace: "konnected-io", author: "konnected.io", mnmn: "SmartThings", vid: "generic-switch") {
	capability "Alarm"
    capability "Switch"
    capability "Actuator"
    capability "Momentary"
    capability "Tone"
      
    command "customBeep", [
      [name: "Duration*", type: "NUMBER", range: "10..*", description: "Each beep or blink duration"],
      [name: "Pause*", type: "NUMBER", range: "10..*", description: "Pause between beeps/blinks in milliseconds"],
      [name: "Repeat*", type: "NUMBER", range: "1..*", description: "Times to repeat the pulse"]
    ]
  }

  preferences {
    input name: "invertTrigger", type: "bool", title: "Low Level Trigger",
          description: "Select if the attached device or relay uses a low-level trigger. Default is high-level trigger"

    // settings for momentary beep
    input name: "beepDuration", type: "number", title: "Beep Pulse (ms)",
      description: "Each beep or blink duration", range: "10..*"
    input name: "beepPause", type: "number", title: "Beep Pause (ms)",
      description: "Pause between beeps/blinks in milliseconds", range: "10..*"
    input name: "beepRepeat", type: "number", title: "Beep Repeat",
      description: "Times to repeat the pulse", range: "1..*"

	// settings for infinately repeating alarm
    input name: "alarmDuration", type: "number", title: "Alarm Pulse (ms)",
      description: "Tone duration in alarm", range: "10..*"
    input name: "alarmPause", type: "number", title: "Alarm Pause (ms)",
      description: "Pause between tones in alarm", range: "10..*"
  }
}

def updated() {
  parent.updateSettingsOnDevice()
}

def updatePinState(Integer state) {
  if (state == -1) { // represents an infinate alarm activated
    sendEvent(name: "alarm", value: "siren")
  } else if (state == triggerLevel()) {
    sendEvent(name: "switch", value: "on", isStateChange: true, display: false)
    sendEvent(name: "switch", value: "off", isStateChange: true, display: false)
  } else {
    sendEvent(name: "alarm", value: "off")
  }
}

def off() {
  def val = invertTrigger ? 1 : 0
  parent.deviceUpdateDeviceState(device.deviceNetworkId, val)
}

def on() {
  beep()
}

def push() {
  beep()
}

def customBeep(BigDecimal duration, BigDecimal pause, BigDecimal repeat) {
  parent.deviceUpdateDeviceState(device.deviceNetworkId, triggerLevel(), [
    momentary : duration ?: 250,
    pause     : pause ?: 150,
    times     : repeat ?: 3
  ])
}

def beep() {
  parent.deviceUpdateDeviceState(device.deviceNetworkId, triggerLevel(), [
    momentary : beepDuration ?: 250,
    pause     : beepPause ?: 150,
    times     : beepRepeat ?: 3
  ])
}

def siren() {
  parent.deviceUpdateDeviceState(device.deviceNetworkId, triggerLevel(), [
    momentary : alarmDuration ?: 55,
    pause     : alarmPause ?: 45,
    times     : -1
  ])
}

def both() {
  siren()
}

def triggerLevel() {
  return invertTrigger ? 0 : 1
}

def currentBinaryValue() {
  invertTrigger ? 1 : 0
}
