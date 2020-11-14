
def compare(str1, str2):
    lst1 = str1.split('.')
    lst2 = str2.split('.')
    pos = 0
    while pos < len(lst1) and pos < len(lst2):
        if lst1[pos].isdigit() and lst2[pos].isdigit():
            if int(lst1[pos]) > int(lst2[pos]):
                return 1
            elif int(lst2[pos]) > int(lst1[pos]):
                return -1
        else:
            raise Exception('One of the string contains non numerical element')
        pos += 1
    if len(lst1) > len(lst2):
        return 1
    elif len(lst2) > len(lst1):
        return -1
    else:
        return 0





