import sys
import math
import random
import time

# This code is a work in progress and is pretty ugly
# - I fixed a bug that ended up making the core part of the algorithm slower, making it less
#   effective (less # of iterations can run now) so this needs to be optimized.
# - Improvements that could be made off the top of my head:
#   - (performance) Use tuples for coords instead of lists.
#   - (performance) Might be faster to not even have Sim be an object that needs
#     to be initialized. Keep all state in main game loop and change methods to be helper funcs.
#   - Have functions take tuple coords instead of x, y as sep args.
#   - Comment and generally clean up the code.
#   - Various other performance improvements can be made (any time a dict is constructed
#     in the simulation should be refactored somehow -- making dicts is slow).


def distance(x1, y1, x2, y2):
    return math.sqrt( (x2 - x1)**2 + (y2 - y1)**2 )


def printy(*ok):
    print(*ok,  file=sys.stderr)


def get_pos_closest(my_x, my_y, target_x, target_y, max_dist, dist=None):
    """
    get position closest to a target while adhering to distance constraint
    """
    if dist is None:
        dist = distance(my_x, my_y, target_x, target_y)
    if dist == 0.:
        return my_x, my_y
    opp = target_y - my_y
    angle = math.asin(float(opp) / dist)
    to_go = min(max_dist, dist)
    delta_x = math.cos(angle) * to_go
    if target_x < my_x:
        delta_x *= -1

    delta_y = math.sin(angle) * to_go
    next_x = my_x + delta_x
    next_y = my_y + delta_y
    return next_x, next_y


class Sim(object):
    def __init__(self, ash, zombies, humans, target):
        self.failed = False
        self.done = False
        self.zombies = zombies
        self.humans = humans
        self.target = target
        self.ash = ash
        self.score = 1
        self.iter_num = 0
        self.dead = set()

    def move_zombies(self):
        self.humans['ash'] = self.ash
        humans = self.humans.items()
        for zid, zombie in self.zombies.items():
            if zid in self.dead:
                continue

            if self.iter_num == 0:
                next_x = zombie[2]
                next_y = zombie[3]
            else:
                distances = {k: distance(zombie[0], zombie[1], v[0], v[1]) for k, v in humans if k not in self.dead}
                closest = min(distances.items(), key=lambda x: x[1])
                closest_hum = self.humans[closest[0]]
                next_x, next_y = get_pos_closest(
                    zombie[0], zombie[1], closest_hum[0], closest_hum[1], 400, closest[1]
                )
            self.zombies[zid] = (int(next_x), int(next_y), None, None)
        del self.humans['ash']

    def move_ash(self):
        if self.ash[0] == self.target[0] and self.ash[1] == self.target[1]:
            # printy('pick new target')
            self.pick_next_target()

        next_x, next_y = get_pos_closest(
            self.ash[0], self.ash[1], self.target[0], self.target[1], 1000
        )
        # printy("ash wanted to go to {}, so he went from {} to {}".format(
        #     self.target, self.ash, [next_x, next_y]
        # ))
        self.ash = (int(next_x), int(next_y))

    def pick_next_target(self):
        entities = {v for k, v in self.humans.items() if k not in self.dead} | {v for k, v in self.zombies.items() if k not in self.dead}
        # entities = list(self.humans.values()) + list(self.zombies.values()) + [self.ash]
        self.target = random.choice(list(entities))

    def kill_zombies(self):
        # self.zombies = {k: v for k, v in self.zombies.items() if distance(self.ash[0], self.ash[1], v[0], v[1]) > 2000}
        num_killed = 0
        to_del = []
        for k, v in self.zombies.items():
            if distance(self.ash[0], self.ash[1], v[0], v[1]) <= 2000:
                # del self.zombies[k]
                to_del.append(k)
                num_killed += 1
        for k in to_del:
            del self.zombies[k]

        # new_dead = {k for k, v in self.zombies.items() if distance(self.ash[0], self.ash[1], v[0], v[1]) <= 2000}
        return num_killed

    def eat_humans(self):
        zombie_coords = set(c[:2] for c in self.zombies.values())
        self.dead |= {k for k, v in self.humans.items() if v in zombie_coords and k not in self.dead}

    def tick(self):
        self.move_zombies()
        self.move_ash()
        killed = self.kill_zombies()
        self.eat_humans()
        num_humans = len([k for k in self.humans.keys() if k not in self.dead])
        num_zombies = len(self.zombies.keys())
        self.score += (num_humans ** 2 * 10) * killed**2
        # self.score += killed**2
        if num_humans == 0:
            self.failed = True
        if num_zombies == 0:
            self.done = True

    def run(self):
        while 1:
            self.tick()
            self.iter_num += 1
            if self.failed:
                return -1
            if self.done:
                return self.score

# game loop
next_coords = None
while True:
    start = time.time()
    extra = ""
    x, y = [int(i) for i in input().split()]
    human_count = int(input())
    humans = {}
    zombies = {}
    for i in range(human_count):
        human_id, human_x, human_y = [int(j) for j in input().split()]
        humans['h'+str(human_id)] = (human_x, human_y)
    zombie_count = int(input())
    for i in range(zombie_count):
        zombie_id, zombie_x, zombie_y, zombie_xnext, zombie_ynext = [int(j) for j in input().split()]
        zombies['z'+str(zombie_id)] = (zombie_x, zombie_y, zombie_xnext, zombie_ynext)

    if next_coords is None:
        next_coords = (human_x, human_y)

    h_list = list(humans.values())
    z_list = list(zombies.values())
    both = (z_list, h_list) #, h_list)
    # num_tries = int(round(1540 / (len(z_list) + len(h_list)), -1))
    num_tries = int(round(1100 / (len(z_list) + len(h_list)), -1))
    # printy("{} {}".format(num_tries, len(h_list) + len(z_list)))
    ash_pos = (x, y)
    times = []

    best_score = 0
    best_coords = None
    i = 0
    # printy(time.time() - start)
    first = True
    while (time.time() - start) < (.15) or first:
        first = False
        i += 1
        start_coords = random.choice(random.choice(both))
        s = Sim(ash_pos,  zombies.copy(), humans, start_coords)
        score = s.run()
        if score > best_score:
            best_score = score
            best_coords = start_coords

    if best_coords is not None:
        next_coords = best_coords
    else:
        # printy('could not find best')
        next_coords = (human_x, human_y)
        extra = 'fuq'


    # Write an action using print
    # To debug: print("Debug messages...", file=sys.stderr)

    # Your destination coordinates
    print("{} {} {}".format(next_coords[0], next_coords[1], extra))
