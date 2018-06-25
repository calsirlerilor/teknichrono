#!python3

from datetime import datetime
from random import randint

from api.check import (checkCategory, checkCountWithLapIndex, checkCountWithLapNumber, checkDeltaBestInIncreasingOrder, checkDeltaPreviousFilled, checkLaptimeFilled,
                       checkNumberLaps, checkPilotFilled)
from api.laps import (getBestLapsForSession, getLapsForSession, getResultsForSession, printLaps)
from api.ping import ping
from api.session import startSession
from api.session_simulator import SessionSimulator


class FridayMorningTest:
  def __init__(self, championship):
    self.championship = championship
    self.morningTest = SessionSimulator()
    self.morningChrono = SessionSimulator()
    self.boarderCross = self.championship.boarderCross
    self.fake1 = self.championship.fake1
    self.chrono = self.championship.chrono
    self.beacons = self.championship.beacons

  def createSessions(self):
    # Add sessions
    print("---- Create session of Friday morning ----")
    s1 = datetime(2000, 1, 1, 10)
    e1 = datetime(2000, 1, 1, 11)
    event = self.championship.event
    location = self.boarderCross
    chronos = [self.fake1, self.chrono]
    pilots = self.championship.allPilots
    self.morningTest.create('Friday am tests', s1, e1, 'tt', location, event, chronos, pilots)
    s2 = datetime(2000, 1, 1, 11)
    e2 = datetime(2000, 1, 1, 12)
    self.morningChrono.create('Friday am Chrono', s2, e2, 'tt', location, event, chronos, pilots)

  def test(self):
    self.borderCross()

  def borderCross(self):
    self.borderCrossQP()
    self.borderCrossChronos()
    self.checkResults()

  def borderCrossQP(self):
    # descente dans le boarder cross
    # Border cross
    # deux runs d essais (controle transpondeurs)
    # deux runs chronos
    # le meilleur retenu

    # Created sessions earlier and start it here
    startSession(self.morningTest.session['id'], datetime(2000, 1, 1, 10, 0, 30))

    print("---- Test #1 ----")
    # Starts every 20s
    startDelta = 20
    # TODO Check if there should be a specific start order
    # TODO Check if it is acceptable to start manually each rider. If not afke1 should be a real chrono
    # -- Start
    startMinute = 1
    for i in range(11, 90):
      m, s = divmod(i * startDelta, 60)
      h, m = divmod(startMinute + m, 60)
      ping(datetime(2000, 1, 1, 10 + h, m, s, randint(0, 500000)), self.beacons[i]['id'], -99, self.fake1['id'])
    # -- End
    endMinute = startMinute + 2
    for i in range(11, 89):
      delta = int(i / 3) + randint(0, int(i / 3))
      m, s = divmod(i * startDelta + delta, 60)
      h, m = divmod(endMinute + m, 60)
      ping(datetime(2000, 1, 1, 10 + h, m, s, randint(0, 500000)), self.beacons[i]['id'], -99, self.chrono['id'])

    print("---- Test #2 ----")
    # Starts every 20s
    # -- Start
    startMinute = 31
    for i in range(12, 90):
      m, s = divmod(i * startDelta, 60)
      h, m = divmod(startMinute + m, 60)
      ping(datetime(2000, 1, 1, 10 + h, m, s, randint(0, 500000)), self.beacons[i]['id'], -99, self.fake1['id'])
    # -- End
    endMinute = startMinute + 2
    for i in range(12, 88):
      delta = int(i / 3) + randint(0, int(i / 3))
      m, s = divmod(i * startDelta + delta, 60)
      h, m = divmod(endMinute + m, 60)
      ping(datetime(2000, 1, 1, 10 + h, m, s, randint(0, 500000)), self.beacons[i]['id'], -99, self.chrono['id'])

    print("---- Tests Results ----")

    #  ---- Results for display ----

    # 10 does not do #1 and #2
    # 11 does not do #2
    # 89 does not finish #1 and #2
    # 88 does not finish #2

    friMorningTestsLaps = getLapsForSession(self.morningTest.session['id'])
    printLaps(friMorningTestsLaps, True)
    checkNumberLaps(friMorningTestsLaps, 160 - 6)
    checkPilotFilled(friMorningTestsLaps)
    checkCountWithLapIndex(friMorningTestsLaps, 1, 78)
    checkCountWithLapIndex(friMorningTestsLaps, 2, 76)
    checkCountWithLapNumber(friMorningTestsLaps, 1, 2)
    checkLaptimeFilled(friMorningTestsLaps)

    friMorningTestsLapsElite = getLapsForSession(self.morningTest.session['id'], self.championship.eliteCategory['id'])
    printLaps(friMorningTestsLapsElite, True)
    checkNumberLaps(friMorningTestsLapsElite, 60 - 3)
    checkCategory(friMorningTestsLapsElite, "Elite")
    checkCountWithLapIndex(friMorningTestsLapsElite, 1, 30 - 1)
    checkCountWithLapIndex(friMorningTestsLapsElite, 2, 30 - 2)
    checkLaptimeFilled(friMorningTestsLapsElite)

    friMorningTestsBests = getBestLapsForSession(self.morningTest.session['id'])
    printLaps(friMorningTestsBests, True)
    checkNumberLaps(friMorningTestsBests, 78)
    checkPilotFilled(friMorningTestsBests)
    checkCountWithLapNumber(friMorningTestsBests, 1, 2)
    checkLaptimeFilled(friMorningTestsBests)
    checkDeltaBestInIncreasingOrder(friMorningTestsBests)
    checkDeltaPreviousFilled(friMorningTestsBests)

  def borderCrossChronos(self):
    # Some do 1 test
    # Some dont test
    # Some start but dont finish
    # Some finish after expected time

    #  ---- Determine startup ----

    friMorningTestsResults = getResultsForSession(self.morningTest.session['id'])
    printLaps(friMorningTestsResults, True)
    # TODO Have chart with startup list
    # TODO Check if it should count points

    checkNumberLaps(friMorningTestsResults, 80)
    checkPilotFilled(friMorningTestsResults)
    checkCountWithLapIndex(friMorningTestsResults, 0, 2)
    checkCountWithLapNumber(friMorningTestsResults, 0, 2)
    checkLaptimeFilled(friMorningTestsResults, True)
    checkDeltaBestInIncreasingOrder(friMorningTestsResults, True)
    checkDeltaPreviousFilled(friMorningTestsResults, True)

    beaconsStartOrder = []
    for i in reversed(range(30)):
      beaconsStartOrder.append(friMorningTestsResults[i]['pilot']['beaconNumber'])
    for i in range(30, 80):
      beaconsStartOrder.append(friMorningTestsResults[i]['pilot']['beaconNumber'])

    startSession(self.morningChrono.session['id'], datetime(2000, 1, 1, 11, 10, 00))
    print("---- Chrono #1 ----")
    # TODO Make start order the one of the previous results
    # Starts every 20s
    startDelta = 20
    # -- Start
    startMinute = 11
    for i in range(1, 80):
      m, s = divmod(i * startDelta, 60)
      h, m = divmod(startMinute + m, 60)
      beaconId = self.beacons[beaconsStartOrder[i]]['id']
      ping(datetime(2000, 1, 1, 11 + h, m, s, randint(0, 500000)), beaconId, -99, self.fake1['id'])
    # -- End
    endMinute = startMinute + 2
    for i in range(1, 79):
      delta = int(i / 3) + randint(0, int(i / 3))
      m, s = divmod(i * startDelta + delta, 60)
      h, m = divmod(endMinute + m, 60)
      beaconId = self.beacons[beaconsStartOrder[i]]['id']
      ping(datetime(2000, 1, 1, 11 + h, m, s, randint(0, 500000)), beaconId, -99, self.chrono['id'])

    print("---- Chrono #2 ----")
    # Starts every 20s
    # -- Start
    startMinute = 45
    for i in range(2, 80):
      m, s = divmod(i * startDelta, 60)
      h, m = divmod(startMinute + m, 60)
      beaconId = self.beacons[beaconsStartOrder[i]]['id']
      ping(datetime(2000, 1, 1, 11 + h, m, s, randint(0, 500000)), beaconId, -99, self.fake1['id'])
    # -- End
    endMinute = startMinute + 2
    for i in range(2, 78):
      delta = int(i / 3) + randint(0, int(i / 3))
      m, s = divmod(i * startDelta + delta, 60)
      h, m = divmod(endMinute + m, 60)
      beaconId = self.beacons[beaconsStartOrder[i]]['id']
      ping(datetime(2000, 1, 1, 11 + h, m, s, randint(0, 500000)), beaconId, -99, self.chrono['id'])

    print("---- Chrono Results ----")

    # ---- Results ----
    # ---- Checks - Asserts ----
    friMorningChronoLaps = getLapsForSession(self.morningChrono.session['id'])
    printLaps(friMorningChronoLaps, True)
    checkNumberLaps(friMorningChronoLaps, 160 - 6)
    checkPilotFilled(friMorningChronoLaps)
    checkCountWithLapIndex(friMorningChronoLaps, 1, 78)
    checkCountWithLapIndex(friMorningChronoLaps, 2, 76)
    checkCountWithLapNumber(friMorningChronoLaps, 1, 2)
    checkLaptimeFilled(friMorningChronoLaps)

    friMorningChronoBests = getBestLapsForSession(self.morningChrono.session['id'])
    printLaps(friMorningChronoBests, True)
    checkNumberLaps(friMorningChronoBests, 78)
    checkPilotFilled(friMorningChronoBests)
    checkCountWithLapNumber(friMorningChronoBests, 1, 2)
    checkLaptimeFilled(friMorningChronoBests)
    checkDeltaBestInIncreasingOrder(friMorningChronoBests)
    checkDeltaPreviousFilled(friMorningChronoBests)

    friMorningChronoResults = getResultsForSession(self.morningChrono.session['id'])
    printLaps(friMorningChronoResults, True)
    checkNumberLaps(friMorningChronoResults, 80)
    checkPilotFilled(friMorningChronoResults)
    checkCountWithLapIndex(friMorningChronoResults, 0, 2)
    checkCountWithLapNumber(friMorningChronoResults, 0, 2)
    checkLaptimeFilled(friMorningChronoResults, True)
    checkDeltaBestInIncreasingOrder(friMorningChronoResults, True)
    checkDeltaPreviousFilled(friMorningChronoResults, True)

    # Some do 1 chrono
    # Some dont chrono
    # Some finish after expected time

    # Some do 1 test
    # Some dont test
    # Some start but dont finish
    # Some finish after expected time

  def checkResults(self):
    friMorningTestsResults = getResultsForSession(self.morningTest.session['id'])
    printLaps(friMorningTestsResults, True)

    checkNumberLaps(friMorningTestsResults, 80)
    checkPilotFilled(friMorningTestsResults)
    checkCountWithLapIndex(friMorningTestsResults, 0, 2)
    checkCountWithLapNumber(friMorningTestsResults, 0, 2)
    checkLaptimeFilled(friMorningTestsResults, True)
    checkDeltaBestInIncreasingOrder(friMorningTestsResults, True)
    checkDeltaPreviousFilled(friMorningTestsResults, True)
