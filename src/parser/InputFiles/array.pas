program arrayTest(input, output);    {Sample tvi code here}
var
   m : array[1..5] of integer;
   n : real;
begin
        m[1] := 1;
        m[2] := 2;
        m[5] := 3;
        m[4] := 4;
        m[5] := 5;
        n := 23e10;

        write(m[1]);
        write(m[2]);
        write(m[3]);
        write(m[4]);
        write(m[5])
end.