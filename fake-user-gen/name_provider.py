first_names = []
last_names = []
    
def read_file(filepath, output):
    data = open(filepath, 'r')
    for line in data.readlines():
        output.append(line.replace('\n', ''))

def get_rand_name():
    from secrets import choice
    return choice(first_names), choice(last_names)

def main():
    for _ in range(0, 10):
        print(get_rand_name())

# always run, whether imported or not
read_file('./names/firstnames.txt', first_names)
read_file('./names/surnames.txt', last_names)

if __name__ == '__main__':
    main()

