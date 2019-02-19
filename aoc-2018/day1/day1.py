def freq_twice(nums):
    seen = set()
    i = 0
    total = 0

    while 1:
        if i > (len(nums) -1):
            i = 0

        total += nums[i]
        i += 1

        if total in seen:
            return total

        seen.add(total)

if __name__ == '__main__':
    with open('input.txt', 'rb') as f:
        input_ints = [int(l) for l in f.readlines()]

    # part 1
    print(sum(input_ints))
    # part 2
    print(freq_twice(input_ints))
