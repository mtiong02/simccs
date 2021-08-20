// Attribute;
// VB_Name = "MIP_SimCCS_TIME";
// TODO: Option Explicit ... Warning!!! not translated
// long n;
// ,long x;
// ,long y;
// ,long d;
// ,long t;
// ,long tau;
// ,long dp;
// ,long g;
// long decimalMultiplier;
// int handle;
// ,string readString;
// ,string[] stringArray;
// float[] pipeArray;
// float co2Divider;
// ,float costDivider;
// float targetCO2;
// Privatetempval;
// ,temp1;
// ,temp2;
// string pipeID;
// ,string pipeIDp;
// Dictionary dictConstraints;
// ,Dictionary dictVariables;

public class ModelSimCCS_TIME {
    // Write MPS for Model
    public final void ModelSimCCS_TIME(
                string savePath,
                string targetString,
                long[] arcArray,
                float target,
                boolean fixedSource,
                boolean variableSource,
                boolean creditSource,
                boolean fixedPipe,
                boolean variablePipe,
                boolean creditPipe,
                boolean fixedSink,
                boolean variableSink,
                boolean creditSink,
                boolean minFlowRequirement,
                boolean onePipeOnly,
                boolean partialCO2,
                boolean roundCosts,
                boolean roundCO2,
                float[] inPipeArray,
                float gap,
                float timeLimit,
                float projectLength,
                Dictionary dictFixedSolution,
                int handleMPS,
                float interest,
                string dumpFile,
                boolean simCCStax,
                boolean usingCPLEX,
                boolean sampleSinkCapacity,
                boolean sampleSinkCost,
                int rSink,
                void rSouce,
                string pathSample,
                boolean uniArc,
                boolean VIs,
                boolean startingSolution) {
        // All CAPACITY units are in MT
        // All MONETARY units are in $MILLION
        string formatText;
        boolean utilityAdjust;
        float totalTarget;
        formatText = "0.000";
        utilityAdjust = true;
        totalTarget = timeArray(4, UBound(timeArray, 2));
        // Dictionary to check if a particular cosntraint exists
        dictConstraints = new Dictionary();
        // Constraint checks
        byte constraint2;
        byte constraint6;
        boolean integerSi;
        long nWells;
        long nGens;
        constraint2 = 1;
        constraint6 = 1;
        integerSi = false;
        float[] arrayCRF;
        float crf;
        float discountRate;
        // interest = 0.09
        discountRate = 0.07;
        // interest = 0.1
        // discountRate = 0
        object arrayCRF;
        for (t = 1; (t <= UBound(arrayCRF)); t++) {
            arrayCRF[t] = (interest
                        * ((interest + 1)
                        | (timeArray(3, t)
                        / ((interest + 1)
                        | (timeArray(3, t) - 1)))));
            // TODO: Warning!!! The operator should be an XOR ^ instead of an OR, but not available in CodeDOM
            // TODO: Warning!!! The operator should be an XOR ^ instead of an OR, but not available in CodeDOM
        }

        string thinningName;
        string mipName;
        long n1;
        long n2;
        long upperT;
        upperT = UBound(timeArray, 2);
        // Discounting arrays
        float[] discountV;
        float[] discountF;
        float Ot;
        float Pt;
        float tempNum;
        float upperAmortized;
        long fromYear;
        long toYear;
        object discountV;
        object discountF;
        upperAmortized = timeArray(3, 1);
        fromYear = 1;
        // Year to start counting from
        for (t = 1; (t <= upperT); t++) {
            // get values from timeArray
            Ot = timeArray(2, t);
            // Number of years in time period
            Pt = timeArray(3, t);
            // Number of years to amortize over
            tempNum = 0;
            // Calculate discounting coefficient for fixed costs
            fromYear = (fromYear + timeArray(2, (t - 1)));
            // Cumulatively calcualte fromYear
            toYear = upperAmortized;
            for (tau = fromYear; (tau <= toYear); tau++) {
                // tempNum = tempNum + ((1 + interest) ^ (0 - t))
                tempNum = (tempNum
                            + ((1 + discountRate) | (0 - tau)));
                // TODO: Warning!!! The operator should be an XOR ^ instead of an OR, but not available in CodeDOM
            }

            // Store the coefficient
            discountF[t] = tempNum;
            tempNum = 0;
            // Calculate discounting coefficient for fixed costs
            toYear = (fromYear
                        + (Ot - 1));
            for (tau = fromYear; (tau <= toYear); tau++) {
                // tempNum = tempNum + ((1 + discountRate) ^ (0 - t))
                tempNum = (tempNum
                            + ((1 + discountRate) | (0 - tau)));
                // TODO: Warning!!! The operator should be an XOR ^ instead of an OR, but not available in CodeDOM
            }

            // Store the coefficient
            discountV[t] = tempNum;
        }

        // Check for only one time period
        // If upperT = 1 Then
        //     discountF(1) = 1
        //     discountV(1) = 1
        // End If
        boolean extraConstraints;
        extraConstraints = false;
        boolean useBounds;
        useBounds = false;
        float sinkCapacity;
        float sinkCost;
        float sourceCapacity;
        int randSink;
        int randSource;
        // Calculate what is the most number of sinks that could ever be deployed
        Dictionary dictWellBound;
        float shortestTime;
        dictWellBound = new Dictionary();
        // TODO: # ... Warning!!! not translated
        for (t = 1; (t <= upperT); t++) {
            if ((timeArray(2, t) < shortestTime)) {
                shortestTime = timeArray(2, t);
            }

        }

        for (entry : dictWellInjectivity) {
            // Fill sink entirely in one time period
            dictWellBound.Item[entry] = (Int(((dictSiteCapacity.Item[entry]
                            / (shortestTime / dictWellInjectivity.Item[entry]))
                            - 0.001)) + 1);
            dictWellBound.Item[entry] = (Int(((dictSiteCapacity.Item[entry]
                            / (shortestTime / dictWellInjectivity.Item[entry]))
                            - 0.001)) + 1);
        }

        // Check the sink annual/total capacities and standard deviation
        Dictionary dictAnnualSinkCapacity;
        float iCap;
        float iRate;
        Dictionary dictAnnualSinkDeviation;
        Dictionary dictSinkAnnualCostVar;
        Dictionary dictSinkAnnualCostFix;
        string fromNode2;
        string toNode2;
        dictAnnualSinkCapacity = new Dictionary();
        dictAnnualSinkDeviation = new Dictionary();
        dictSinkAnnualCostVar = new Dictionary();
        dictSinkAnnualCostFix = new Dictionary();
        // Setup values
        for (entry : dictWellInjectivity) {
            // Select random number
            randSink = (Int((Rnd * rSink)) + 1);
            // Get annual injection rate and max capacity
            iRate = dictWellInjectivity.Item[entry];
            // Max injection rate
            iCap = (dictSiteCapacity.Item[entry] / CO2Length);
            // Total capacity
            // Check if we're using total capacity divide by project length...
            if ((iCap <= iRate)) {
                dictAnnualSinkCapacity.Item[entry] = iCap;
                dictAnnualSinkDeviation.Item[entry] = dictICapDev.Item[entry];
                // ... or if we're bounded by the annul injectivity
            }
            else {
                dictAnnualSinkCapacity.Item[entry] = iRate;
                dictAnnualSinkDeviation.Item[entry] = dictIRateDev.Item[entry];
            }

            // Now check if we're sampling
            if ((sampleSinkCapacity == true)) {
                dictAnnualSinkCapacity.Item[entry] = GetSampleNumber((pathSample + ("\\Uncertainty\\Sinks\\Sink"
                                + (entry.ToString() + ".txt"))), randSink, 2);
                // dictAnnualSinkCapacity.Item(entry) = NormalDistribution(dictAnnualSinkCapacity.Item(entry), dictAnnualSinkDeviation.Item(entry))
            }

            // Now check if we're sampling cost
            if ((sampleSinkCost == true)) {
                dictSinkAnnualCostVar.Item[entry] = GetSampleNumber((pathSample + ("\\Uncertainty\\Sinks\\Sink"
                                + (entry.ToString() + ".txt"))), randSink, 3);
                // dictSinkAnnualCostVar.Item(entry) = NormalDistribution(dictSinkVariableCost.Item(entry), dictICostVDev.Item(entry))
                dictSinkAnnualCostFix.Item[entry] = NormalDistribution(dictWellFixed.Item[entry], dictICostFDev.Item[entry]);
            }
            else {
                dictSinkAnnualCostVar.Item[entry] = dictWellVariableOM.Item[entry];
                dictSinkAnnualCostFix.Item[entry] = dictWellFixed.Item[entry];
            }

            // Check if we have REAL costs and capacities for the burning matrix
            if ((dictRealRcost.Exists(entry) == true)) {
                dictSinkAnnualCostFix.Item[entry] = dictRealRcost.Item[entry];
            }

            // Fixed cost
            if ((dictRealBcost.Exists(entry) == true)) {
                dictSinkAnnualCostVar.Item[entry] = dictRealBcost.Item[entry];
            }

            // Variable cost
            if ((dictRealRcapacity.Exists(entry) == true)) {
                dictAnnualSinkCapacity.Item[entry] = dictRealRcapacity.Item[entry];
            }

            // Sink capacity
        }

        // Store the incoming array of pipeline diamters/costs/capacities to use
        pipeArray = inPipeArray;
        // Set the uuper pipeline bound and morbee arrays
        int dBound;
        float morbeeCON;
        float morbeeROW;
        float[] arrayPipeMax;
        float[] arrayPipeMin;
        float totalLength;
        float totalCON;
        float totalROW;
        boolean useM;
        useM = false;
        if ((morbee == true)) {
            dBound = UBound(arrayMorbee, 2);
            object arrayPipeMax;
            object arrayPipeMin;
        }

        if ((morbee == false)) {
            dBound = UBound(pipeArray, 2);
        }

        // Adjust tax
        targetCO2 = target;
        // ROW and construction costs
        float costROW;
        float costCONS;
        float coefficient;
        // Check whether a bounds section should be written for 0,1 variables
        boolean writeBounds;
        writeBounds = false;
        long[] lpArcArray;
        long nSources;
        long nSinks;
        long nNodes;
        long newNode;
        int dumpHandle;
        // Dim fromNode As Long, toNode As Long
        string fromNode;
        string toNode;
        long sourceNode;
        long sinkNode;
        float pathCost;
        float pathLength;
        string itemString;
        float fixedCost;
        float variableCost;
        float operatingCost;
        string variableIndex;
        // Make a dictionary of which arcs are connected to each node and a list of all arcs
        Dictionary dictKfromI;
        Dictionary dictK;
        string fromString;
        string toString;
        long node1;
        long node2;
        string nodeString1;
        string nodeString2;
        string letterJK;
        Dictionary dictLeavingI;
        Dictionary dictEnteringI;
        dictLeavingI = new Dictionary();
        dictEnteringI = new Dictionary();
        dictKfromI = new Dictionary();
        dictK = new Dictionary();
        // get list of arcs atatched to each node
        for (n = 1; (n <= UBound(arcArray, 1)); n++) {
            if ((arcArray(n, 0) > 0)) {
                fromNode = dictNodesMap1.Item[n];
                // From node
                for (x = 1; (x <= arcArray(n, 0)); x++) {
                    toNode = dictNodesMap1.Item[arcArray(n, x)];
                    // From node
                    // Store in the Yij dictionary
                    dictLeavingI.Item[fromNode] = (dictLeavingI.Item[fromNode] + ('\t' + toNode));
                    // Store in the Yij dictionary
                    dictEnteringI.Item[toNode] = (dictLeavingI.Item[fromNode] + ('\t' + fromNode));
                    if ((n < arcArray(n, x))) {
                        fromString = dictNodesMap1.Item[n];
                        // From node
                        toString = dictNodesMap1.Item[arcArray(n, x)];
                        // To node node
                        // Store this arc
                        dictK.Add;
                        (fromString + ('\t' + toString));
                        true;
                    }
                    else {
                        toString = dictNodesMap1.Item[n];
                        // From node
                        fromString = dictNodesMap1.Item[arcArray(n, x)];
                        // To node node
                    }

                    // Check if this fromNode already exists
                    if ((dictKfromI.Exists(fromNode) == false)) {
                        object stringArray;
                        stringArray[0] = (fromString + ('\t' + toString));
                        // Place toNode into stringArray
                        dictKfromI.Add;
                        fromNode;
                        stringArray;
                        // Store the stringArray
                    }
                    else {
                        // Update stringArray
                        stringArray = dictKfromI.Item[fromNode];
                        // Get the stringArray
                        object Preserve;
                        stringArray[(UBound(stringArray) + 1)];
                        // Expand stringArray
                        stringArray[UBound(stringArray)] = (fromString + ('\t' + toString));
                        // Store the new toNode
                        dictKfromI.Item[fromNode] = stringArray;
                        // Store the new stringArray
                    }

                }

            }

        }

        //     'Create a dictionary to store the number of wells for each sink
        //     'The number may have to be adjusted if the defined max number exceeds the reservoir capacity when multiplied by the project length
        //     Dim dictWellNumberNew As Dictionary, wellNum As Long, wellCapacity As Single
        //     Set dictWellNumberNew = New Dictionary
        //     For Each entry In dictSinks
        //         wellNum = dictWellNumber.Item(entry)
        //         wellCapacity = dictWellCapacity.Item(entry)
        //         sinkCapacity = dictSinkCapacity.Item(entry)
        //
        //         'Check if the number of wells over this project length exceeds total capacity
        //         If CSng(wellNum) * wellCapacity * CSng(projectLength) > sinkCapacity Then
        //             wellNum = Int(sinkCapacity / (CSng(wellNum) * wellCapacity))
        //             dictWellNumberNew.Item(entry) = wellNum
        //         Else
        //             'Just store the original number of wells
        //             dictWellNumberNew.Item(entry) = dictWellNumber.Item(entry)
        //         End If
        //     Next
        // Get total amount of CO2 that can be capture in the system annually
        float totalCO2;
        totalCO2 = 0;
        for (entry : dictSources) {
            // Store CO2 amount
            totalCO2 = (totalCO2 + dictSourceCapacity.Item[entry]);
        }

        // Number of dources/sinks/nodes
        nSources = dictSources.Count;
        nSinks = dictSinks.Count;
        nNodes = dictNodes.Count;
        // Set up costs for SINK vs. WELL
        Dictionary dictSinkFixedCostNew;
        Dictionary dictSinkVariableCostNew;
        Dictionary dictSinkCapacityNew;
        Dictionary dictSinkCreditNew;
        string[] listOfArcs;
        dictSinkFixedCostNew = dictWellFixed;
        dictSinkVariableCostNew = dictWellVariableOM;
        dictSinkCapacityNew = dictSiteCapacity;
        dictSinkCreditNew = dictSinkCredit;
        // Cost/capacity adjustments
        if ((roundCosts == true)) {
            costDivider = 1000;
        }
        else {
            costDivider = 1;
        }

        if ((roundCO2 == true)) {
            co2Divider = 1000;
        }
        else {
            co2Divider = 1;
        }

        decimalMultiplier = 0;
        // ZERO decimal places
        // Create a dictionary containing UNCAPACITATED arcs that are burnt in
        Dictionary dictBurnArcs;
        dictBurnArcs = new Dictionary();
        for (entry : dictBurnY) {
            dictBurnArcs.Item[entry.Substring(0, 6)] = true;
        }

        // Calculate the morbee coefficients
        float morbeeLength;
        if ((morbee == true)) {
            for (d = 1; (d
                        <= (dBound - 1)); d++) {
                // Calcualte max pipe capacity based on intersection of CONSTRUCTION linear pieces
                arrayPipeMax[d] = ((arrayMorbee(2, (d + 1)) - arrayMorbee(2, d))
                            / (arrayMorbee(1, d) - arrayMorbee(1, (d + 1))));
                if ((arrayPipeMax[d] > target)) {
                    arrayPipeMax[d] = totalTarget;
                }

                if ((utilityAdjust == true)) {
                    arrayPipeMax[d] = (arrayPipeMax[d] * pipeUtilization);
                }

                // Set the minimum pipeline capacity
                arrayPipeMin[(d + 1)] = arrayPipeMax[d];
            }

            // Need to set the pipe max for the last category
            arrayPipeMax[dBound] = 83.95;
            // If arrayPipeMax(d) > target Then arrayPipeMax(d) = target
            if ((arrayPipeMax[d] > target)) {
                arrayPipeMax[d] = ((totalTarget / pipeUtilization)
                            * 1.01);
            }

            // ''''''''''''
            // ''  MPS  '''
            // ''''''''''''
            // '''''''''''''''''''''''''
            //                         '
            //  M     M  PPPP     SSSS '
            //  MM   MM  P   P   S   ' '
            //  M M M M  P    P  S     '
            //  M  M  M  P   P    SSS  '
            //  M    'M  PPPP        S '
            //  M    'M  P           S '
            //  M    'M  P           S '
            //  M    'M  P       SSSS' '
            //                         '
            // '''''''''''''''''''''''''
            // Use the array to calculate how many space we should put in the file for each variable
            // MPS files are fixed width files
            long sourceID;
            long sinkID;
            string[,] spaceArray;
            for (y = 1; (y <= 25); y++) {
                for (x = 1; (x <= y); x++) {
                    spaceArray[y] = (spaceArray[y] + " ");
                }
            }

            // Calculate the total capacity of storage in each cell
            Dictionary dictCapacityR;
            dictCapacityR = new Dictionary();
            for (entry : dictSinksInCell) {
                fromNode = dictNodesMap1.Item[entry];
                stringArray = dictSinksInCell.Item[entry];
                // get list of sinks
                for (g = 1; (g <= UBound(stringArray)); g++) {
                    sinkID = long.Parse(stringArray[g]);
                    dictCapacityR.Item[sinkID] = (dictCapacityR.Item[sinkID]
                                + (dictSiteCapacity.Item[sinkID] / projectLength));
                }

            }

            // Set dictionaries for cosntraints/variables
            dictConstraints = new Dictionary();
            dictVariables = new Dictionary();
            // Start writing the MPS file
            handle = FreeFile;
            if ((networkName == "Network")) {
                mipName = "time";
                Open;
                (savePath
                            + (mipName + ".mps"));
                for (object Output; ; Output++) {
                    // TODO: # ... Warning!!! not translated
                    handle;
                    networkName = "NetworkDirect";
                    mipName = ("timeD" + targetString);
                    Open;
                    (savePath
                                + (mipName + ".mps"));
                    for (object Output; ; Output++) {
                        // TODO: # ... Warning!!! not translated
                        handle;
                        // Get the name of the thinning regime
                        stringArray = networkName.Split(" ");
                        thinningName = stringArray[1];
                        mipName = ("time_"
                                    + (thinningName + ("_" + targetString)));
                        Open;
                        (savePath
                                    + (mipName + ".mps"));
                        for (object Output; ; Output++) {
                            // TODO: # ... Warning!!! not translated
                            handle;
                            if (Print) {
                                // TODO: # ... Warning!!! not translated
                                handle;
                                "NAME          Time";
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handle;
                                "ROWS";
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handle;
                                " N  OBJ";
                                for (entry : dictCost) {
                                    stringArray = entry.Split('\t');
                                    // Entry
                                    fromNode = dictNodesMap1.Item[long.Parse(stringArray[0])];
                                    // From node
                                    toNode = dictNodesMap1.Item[long.Parse(stringArray[1])];
                                    // To node node
                                    // Loop through time periods
                                    for (t = 1; (t <= upperT); t++) {
                                        // Print the constraint name
                                        if ((constraint2 == 1)) {
                                            Print;
                                            // TODO: # ... Warning!!! not translated
                                            handle;
                                            (" L  A"
                                                        + (fromNode + (","
                                                        + (toNode + t.ToString()))));
                                            dictConstraints.Add;
                                            ("A"
                                                        + (fromNode + (","
                                                        + (toNode + t.ToString()))));
                                            true;
                                        }
                                        else if ((constraint2 == 2)) {
                                            if ((long.Parse(stringArray[0]) < long.Parse(stringArray[1]))) {
                                                Print;
                                                // TODO: # ... Warning!!! not translated
                                                handle;
                                                (" L  A"
                                                            + (fromNode + (","
                                                            + (toNode + t.ToString()))));
                                                dictConstraints.Add;
                                                ("A"
                                                            + (fromNode + (","
                                                            + (toNode + t.ToString()))));
                                                true;
                                            }

                                        }

                                    }

                                }

                                // Rows B (CO2 flow must be less than max pipeline capacity)
                                // Xij -Sum[QidYijd] <= 0
                                if ((minFlowRequirement == true)) {
                                    for (entry : dictCost) {
                                        stringArray = entry.Split('\t');
                                        // Entry
                                        fromNode = dictNodesMap1.Item[long.Parse(stringArray[0])];
                                        // From node
                                        toNode = dictNodesMap1.Item[long.Parse(stringArray[1])];
                                        // To node node
                                        // Loop through time periods
                                        for (t = 1; (t <= upperT); t++) {
                                            // Print the constraint name
                                            Print;
                                            // TODO: # ... Warning!!! not translated
                                            handle;
                                            (" G  B"
                                                        + (fromNode + (","
                                                        + (toNode + t.ToString()))));
                                            dictConstraints.Add;
                                            ("B"
                                                        + (fromNode + (","
                                                        + (toNode + t.ToString()))));
                                            true;
                                        }

                                    }

                                }

                                // Rows C (Mass balance 0 also records CO2 amount leaving/entering sources/sinks)
                                // Sum[Xi- - Sum[Xji] - Ai =0
                                for (entry : dictNodes) {
                                    fromNode = dictNodesMap1.Item[entry];
                                    // Node
                                    for (t = 1; (t <= upperT); t++) {
                                        Print;
                                        // TODO: # ... Warning!!! not translated
                                        handle;
                                        (" E  C"
                                                    + (fromNode + t.ToString()));
                                        dictConstraints.Add;
                                        ("C"
                                                    + (fromNode + t.ToString()));
                                        true;
                                    }

                                }

                                // Rows D
                                // Ait <= Sum[QiSit]
                                for (entry : dictSources) {
                                    for (t = 1; (t <= upperT); t++) {
                                        if ((partialCO2 == true)) {
                                            Print;
                                            // TODO: # ... Warning!!! not translated
                                            handle;
                                            (" L  D"
                                                        + (entry.ToString() + t.ToString()));
                                        }
                                        else {
                                            Print;
                                            // TODO: # ... Warning!!! not translated
                                            handle;
                                            (" E  D"
                                                        + (entry.ToString() + t.ToString()));
                                        }

                                        dictConstraints.Add;
                                        ("D"
                                                    + (entry.ToString() + t.ToString()));
                                        true;
                                    }

                                }

                                // Rows E
                                // OtBt <= Sum[QjRjt]
                                for (entry : dictSinks) {
                                    for (t = 1; (t <= upperT); t++) {
                                        Print;
                                        // TODO: # ... Warning!!! not translated
                                        handle;
                                        (" L  E"
                                                    + (entry.ToString() + t.ToString()));
                                        dictConstraints.Add;
                                        ("E"
                                                    + (entry.ToString() + t.ToString()));
                                        true;
                                    }

                                }

                                // This constraint is NOT needed for SimCCS-tax
                                if ((simCCStax == false)) {
                                    // Rows F
                                    // Sum[Ai] = T
                                    for (t = 1; (t <= upperT); t++) {
                                        // Check if we allow partial capture amount from each source
                                        if ((partialCO2 == true)) {
                                            Print;
                                            // TODO: # ... Warning!!! not translated
                                            handle;
                                            (" E  F" + t.ToString());
                                        }
                                        else {
                                            Print;
                                            // TODO: # ... Warning!!! not translated
                                            handle;
                                            (" G  F" + t.ToString());
                                        }

                                        dictConstraints.Item["F"] = true;
                                    }

                                }

                                // Add a CONSTANT constrain for simTax (cost to NOT capture CO2)
                                if ((simCCStax == true)) {
                                    // Rows T
                                    Print;
                                    // TODO: # ... Warning!!! not translated
                                    handle;
                                    " E  T";
                                    dictConstraints.Add;
                                    "F";
                                    true;
                                }

                                // Rows G
                                // Sum[Ykdy] <= 1
                                if ((onePipeOnly == true)) {
                                    for (entry : dictCost) {
                                        stringArray = entry.Split('\t');
                                        // Entry
                                        fromNode = dictNodesMap1.Item[long.Parse(stringArray[0])];
                                        // From node
                                        toNode = dictNodesMap1.Item[long.Parse(stringArray[1])];
                                        // To node node
                                        // Loop through time periods
                                        for (t = 1; (t <= upperT); t++) {
                                            // Print the constraint name
                                            Print;
                                            // TODO: # ... Warning!!! not translated
                                            handle;
                                            (" L  G"
                                                        + (fromNode + (","
                                                        + (toNode + t.ToString()))));
                                            dictConstraints.Add;
                                            ("G"
                                                        + (fromNode + (","
                                                        + (toNode + t.ToString()))));
                                            true;
                                        }

                                    }

                                }

                                // Rows H
                                // Sum[Ait] <= 1
                                for (entry : dictSources) {
                                    // sourceNode = dictSources.Item(entry)        'Get the node as cell number
                                    // fromNode = dictNodesMap1.Item(sourceNode)   'Node
                                    Print;
                                    // TODO: # ... Warning!!! not translated
                                    handle;
                                    (" L  H" + entry.ToString());
                                    dictConstraints.Add;
                                    ("H" + entry.ToString());
                                    true;
                                }

                                // Rows I
                                // OtBt <= Sum[QjRjt]
                                for (entry : dictSinks) {
                                    // sinkNode = dictSinks.Item(entry)        'Get the node as cell number
                                    // fromNode = dictNodesMap1.Item(sinkNode) 'Node
                                    Print;
                                    // TODO: # ... Warning!!! not translated
                                    handle;
                                    (" L  I" + entry.ToString());
                                    dictConstraints.Add;
                                    ("I" + entry.ToString());
                                    true;
                                }

                                // Rows J
                                // Bj <= QjRj or Wj <= PjQj
                                for (entry : dictSinks) {
                                    // Loop through time periods
                                    for (t = 1; (t <= upperT); t++) {
                                        Print;
                                        // TODO: # ... Warning!!! not translated
                                        handle;
                                        (" L  J"
                                                    + (entry.ToString() + t.ToString()));
                                        dictConstraints.Add;
                                        ("J"
                                                    + (entry.ToString() + t.ToString()));
                                        true;
                                    }

                                }

                                // Morbee constaints
                                if ((morbee == true)) {
                                    // Rows K ("route must be opened to build a pipeline")
                                    // Yijk(max) - Qijk >= 0
                                    for (entry : dictCost) {
                                        stringArray = entry.Split('\t');
                                        // Entry
                                        fromNode = dictNodesMap1.Item[long.Parse(stringArray[0])];
                                        // From node
                                        toNode = dictNodesMap1.Item[long.Parse(stringArray[1])];
                                        // To node node
                                        for (t = 1; (t <= upperT); t++) {
                                            for (d = 1; (d <= dBound); d++) {
                                                Print;
                                                // TODO: # ... Warning!!! not translated
                                                handle;
                                                (" G  K"
                                                            + (fromNode
                                                            + (toNode
                                                            + (d.ToString() + t.ToString()))));
                                                dictConstraints.Add;
                                                ("K"
                                                            + (fromNode
                                                            + (toNode
                                                            + (d.ToString() + t.ToString()))));
                                                true;
                                            }

                                        }

                                    }

                                    // Rows L (set minimum pipeline capacity for Q2 pipelines)
                                    // Qijk - Yijk(min) >= 0
                                    // For Each entry In dictCost
                                    //     stringArray = Split(entry, Chr(9))                      'Entry
                                    //     fromNode = dictNodesMap1.Item(CLng(stringArray(0)))     'From node
                                    //     toNode = dictNodesMap1.Item(CLng(stringArray(1)))       'To node node
                                    //     'Only do this for Q2
                                    //     Print #handle, " G  L" + fromNode + toNode + "2"        'Print the constraint name
                                    //     dictConstraints.Add "L" + fromNode + toNode + "2", True 'Stpore constraint
                                    // Next
                                }

                                // Rows M (only one pipeline per route)
                                // Sum[Yijc<=1
                                if (((morbee == true)
                                            && (useM == true))) {
                                    for (entry : dictCost) {
                                        for (t = 1; (t <= upperT); t++) {
                                            stringArray = entry.Split('\t');
                                            // Entry
                                            fromNode = dictNodesMap1.Item[long.Parse(stringArray[0])];
                                            // From node
                                            toNode = dictNodesMap1.Item[long.Parse(stringArray[1])];
                                            // To node node
                                            Print;
                                            // TODO: # ... Warning!!! not translated
                                            handle;
                                            (" L  M"
                                                        + (fromNode + (","
                                                        + (toNode + t.ToString()))));
                                            dictConstraints.Add;
                                            ("M"
                                                        + (fromNode + (","
                                                        + (toNode + t.ToString()))));
                                            true;
                                        }

                                    }

                                }

                                // Burn in variables
                                // Rows Q - pipelines
                                for (entry : dictBurnY) {
                                    Print;
                                    // TODO: # ... Warning!!! not translated
                                    handle;
                                    (" E  Q" + entry);
                                    // Print constraint name
                                }

                                // Rows QS
                                for (entry : dictBurnS) {
                                    Print;
                                    // TODO: # ... Warning!!! not translated
                                    handle;
                                    (" E  QS" + entry);
                                    // Print constraint name
                                }

                                // Rows QA
                                for (entry : dictBurnA) {
                                    Print;
                                    // TODO: # ... Warning!!! not translated
                                    handle;
                                    (" G  QA" + entry);
                                    // Print constraint name
                                }

                                // Rows QR
                                for (entry : dictBurnR) {
                                    Print;
                                    // TODO: # ... Warning!!! not translated
                                    handle;
                                    (" E  QR" + entry);
                                    // Print constraint name
                                }

                                // Rows QB
                                for (entry : dictBurnB) {
                                    Print;
                                    // TODO: # ... Warning!!! not translated
                                    handle;
                                    (" G  QB" + entry);
                                    // Print constraint name
                                }

                                // Rows to handle fixed-in solution
                                // VARIABLES HAVE ALREADY BEEN CONVERTED USING DICTNODESMAP1 (since fixed variables were read out of a CPLEX solution file)
                                for (entry : dictFixedSolution) {
                                    Print;
                                    // TODO: # ... Warning!!! not translated
                                    handle;
                                    (" E  Q" + dictFixedSolution.Item[entry].ToString());
                                }

                                // Rows Z
                                // Dummy variable to store project length, target, and CRF
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handle;
                                " E  Z1";
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handle;
                                " E  Z2";
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handle;
                                " E  Z3";
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handle;
                                " E  Z4";
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handle;
                                "COLUMNS";
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handle;
                                "    INTEGER1  \'MARKER\'                 \'INTORG\'";
                                float tempCost;
                                float tempCapacity;
                                string part1;
                                string part2;
                                string part3;
                                // Write all constraints that contain Yijd
                                for (entry : dictCost) {
                                    stringArray = entry.Split('\t');
                                    // Entry
                                    // Actual node numbers
                                    n1 = long.Parse(stringArray[0]);
                                    n2 = long.Parse(stringArray[1]);
                                    if (((uniArc == true)
                                                && (n1 > n2))) {
                                        goto SkipArc;
                                    }

                                    fromNode = dictNodesMap1.Item[long.Parse(stringArray[0])];
                                    // From node
                                    toNode = dictNodesMap1.Item[long.Parse(stringArray[1])];
                                    // To node node
                                    // Time (loop through t first)
                                    for (t = 1; (t <= upperT); t++) {
                                        // Loop through pipe capacities
                                        // For d = 1 To UBound(pipeArray, 2)
                                        for (d = 1; (d <= dBound); d++) {
                                            // Get the letter ID for the pipe diameter
                                            // pipeID = dictPipeMap1.Item(pipeArray(0, d))
                                            // pipeID = dictPipeMap1.Item(d)
                                            if ((morbee == false)) {
                                                pipeID = dictPipeMap1.Item[d];
                                            }

                                            if ((morbee == true)) {
                                                pipeID = d.ToString();
                                            }

                                            part1 = ("Y"
                                                        + (fromNode
                                                        + (toNode
                                                        + (pipeID + t.ToString()))));
                                            dictVariables.Add;
                                            part1;
                                            "0";
                                            part1 = (part1 + spaceArray[(10 - part1.Length)]);
                                            // Spaces
                                            variableIndex = (fromNode
                                                        + (toNode + pipeID));
                                            // Variable index
                                            // ROW OBJ
                                            // Only print in objective if we're using bi-directional arcs OR fromNode is smaller than toNode
                                            if ((fixedPipe == true)) {
                                                // Cost to build pipeline between i and j
                                                // ROW cost
                                                // cost   = "$/km/ft        * width           * weighted length           * adjustment
                                                // costROW = pipeArray(5, d) * pipeArray(6, d) * dictLengthROW.Item(entry) * multiplierROW
                                                costROW = (pipeArray(5, d)
                                                            * (dictLengthROW.Item[entry]
                                                            * (multiplierROW * crf)));
                                                // CONS cost
                                                // cost    = "$/km           * weighted-length            * adjustment
                                                costCONS = (pipeArray(3, d)
                                                            * (dictLengthCONS.Item[entry] * multiplierCONS));
                                                // Fixed cost, with discount
                                                // Adjust the cost if this arc is already burnt-in
                                                // The burnt in arc MUST be built (and therefore incur ROW costs), but additional pipelines do not
                                                if (((dictBurnArcs.Exists((fromNode + toNode)) == true)
                                                            && (dictBurnY.Exists(variableIndex) == true))) {
                                                    fixedCost = ((costCONS * 0.8)
                                                                * (discountF[t] * arrayCRF[t]));
                                                }
                                                else {
                                                    fixedCost = ((costROW + costCONS)
                                                                * (discountF[t] * arrayCRF[t]));
                                                }

                                                // Operating cost, with discount
                                                operatingCost = (dictLength.Item[entry]
                                                            * (pipeArray(7, d) * discountV[t]));
                                                // Calcualte and print costs
                                                coefficient = (fixedCost
                                                            + (variableCost + operatingCost));
                                                // Coefficient
                                                coefficient = (coefficient * convertToEuros);
                                                // Convert to Euros
                                                // Check for morbee coefficinets in the objective
                                                if ((morbee == true)) {
                                                    // Calculate weight
                                                    operatingCost = (pipeArray(7, 1) * dictLength.Item[entry]);
                                                    // * discountV(t)
                                                    morbeeCON = (arrayMorbee(2, d)
                                                                * (dictLengthCONS.Item[entry] * multiplierCONS));
                                                    morbeeROW = (arrayMorbee(4, d)
                                                                * (dictLengthROW.Item[entry] * multiplierROW));
                                                    morbeeLength = ((dictLengthROW.Item[entry] + (3 * dictLengthCONS.Item[entry]))
                                                                / 4);
                                                    // coefficient = (arrayMorbee(2, d) + operatingCost) * morbeeLength * multiplierROW * crf 'The objective for Yijk is just taken from the arrayMorbee
                                                    coefficient = ((morbeeCON
                                                                + (morbeeROW + operatingCost))
                                                                * (discountF[t] * arrayCRF[t]));
                                                    // The objective for Yijk is just taken from the arrayMorbee
                                                    coefficient = (coefficient * convertToEuros);
                                                    // Convert to Euros
                                                }

                                                // Print line
                                                Print;
                                                // TODO: # ... Warning!!! not translated
                                                handle;
                                                ("    "
                                                            + (part1 + ("OBJ       " + Format(coefficient, formatText))));
                                                // Print line
                                            }

                                            // Rows A
                                            // Variable MUST appear in other A Constraints too (i.e., constraints with higher t values)
                                            if ((morbee == false)) {
                                                if (((constraint2 == 1)
                                                            || (uniArc == true))) {
                                                    for (tau = t; (tau <= upperT); tau++) {
                                                        part2 = ("A"
                                                                    + (fromNode + (","
                                                                    + (toNode + tau.ToString()))));
                                                        part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                                        // Spaces
                                                        tempCapacity = (pipeArray(9, d) * -1);
                                                        // Pipeline capacity
                                                        Print;
                                                        // TODO: # ... Warning!!! not translated
                                                        handle;
                                                        ("    "
                                                                    + (part1
                                                                    + (part2 + Format(tempCapacity, formatText))));
                                                        // Print line
                                                        // If we're using unidirection arcs, then this arc appears in the opposite constraint too
                                                        if ((uniArc == true)) {
                                                            part2 = ("A"
                                                                        + (toNode + (","
                                                                        + (fromNode + tau.ToString()))));
                                                            part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                                            // Spaces
                                                            tempCapacity = (pipeArray(9, d) * -1);
                                                            // Pipeline capacity
                                                            Print;
                                                            // TODO: # ... Warning!!! not translated
                                                            handle;
                                                            ("    "
                                                                        + (part1
                                                                        + (part2 + Format(tempCapacity, formatText))));
                                                            // Print line
                                                        }

                                                    }

                                                }
                                                else if ((constraint2 == 2)) {
                                                    if ((n1 < n2)) {
                                                        for (tau = t; (tau <= upperT); tau++) {
                                                            part2 = ("A"
                                                                        + (fromNode + (","
                                                                        + (toNode + tau.ToString()))));
                                                            part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                                            // Spaces
                                                            tempCapacity = (pipeArray(9, d) * -1);
                                                            // Pipeline capacity
                                                            Print;
                                                            // TODO: # ... Warning!!! not translated
                                                            handle;
                                                            ("    "
                                                                        + (part1
                                                                        + (part2 + Format(tempCapacity, formatText))));
                                                            // Print line
                                                            // If we're using unidirection arcs, then this arc appears in the opposite constraint too
                                                            if ((uniArc == true)) {
                                                                part2 = ("A"
                                                                            + (toNode + (","
                                                                            + (fromNode + tau.ToString()))));
                                                                part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                                                // Spaces
                                                                tempCapacity = (pipeArray(9, d) * -1);
                                                                // Pipeline capacity
                                                                Print;
                                                                // TODO: # ... Warning!!! not translated
                                                                handle;
                                                                ("    "
                                                                            + (part1
                                                                            + (part2 + Format(tempCapacity, formatText))));
                                                                // Print line
                                                            }

                                                        }

                                                    }

                                                }

                                            }

                                            // 'If we're using unidirection arcs, then this arc appears in the opposite constraint too...
                                            // '... AND we're using version 1 of constraint 2(A)
                                            // If uniArc = True And constraint2 = 1 Then
                                            //     part2 = "A" + toNode + "," + fromNode + CStr(tau)                       'Row name
                                            //     part2 = part2 + spaceArray(10 - Len(part2))                             'Spaces
                                            //     tempCapacity = pipeArray(9, d) * -1                                     'Pipeline capacity
                                            //     Print #handle, "    " + part1 + part2 + Format(tempCapacity, formattext)   'Print line
                                            // End If
                                            // Rows B
                                            // Do not exist
                                            // Rows G
                                            if ((onePipeOnly == true)) {
                                                part2 = ("G"
                                                            + (fromNode + (","
                                                            + (toNode + t.ToString()))));
                                                part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                                // Spaces
                                                Print;
                                                // TODO: # ... Warning!!! not translated
                                                handle;
                                                ("    "
                                                            + (part1
                                                            + (part2 + "1.000")));
                                            }

                                            // Morbee constraints
                                            // Rows K
                                            if ((morbee == true)) {
                                                for (tau = t; (tau <= upperT); tau++) {
                                                    tempCapacity = arrayPipeMax[d];
                                                    part2 = ("K"
                                                                + (fromNode
                                                                + (toNode
                                                                + (d.ToString() + tau.ToString()))));
                                                    part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                                    // Spaces
                                                    // Set morbee coefficient
                                                    Print;
                                                    // TODO: # ... Warning!!! not translated
                                                    handle;
                                                    ("    "
                                                                + (part1
                                                                + (part2 + Format(tempCapacity, formatText))));
                                                    // Print line
                                                }

                                                // 'Rows L
                                                // If d = 1 Then tempCapacity = 0 - p1min  'Set minimum pipeline capacity (if opened)
                                                // If d = 2 Then tempCapacity = 0 - p2min  'Set minimum pipeline capacity (if opened)
                                                // If d = 2 Then
                                                //     part2 = "L" + fromNode + toNode + CStr(d)                               'Row name
                                                //     part2 = part2 + spaceArray(10 - Len(part2))                             'Spaces
                                                //     'Set morbee coefficient
                                                //     Print #handle, "    " + part1 + part2 + Format(tempCapacity, formattext)   'Print line
                                                // End If
                                            }

                                            // Rows M
                                            if (((morbee == true)
                                                        && (useM == true))) {
                                                part2 = ("M"
                                                            + (fromNode + (","
                                                            + (toNode + t.ToString()))));
                                                part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                                // Spaces
                                                Print;
                                                // TODO: # ... Warning!!! not translated
                                                handle;
                                                ("    "
                                                            + (part1
                                                            + (part2 + "1.000")));
                                            }

                                            // Rows Q
                                            // Check if Yijd appears in the fixed solution
                                            // Only print in objective if we're using bi-directional arcs OR fromNode is smaller than toNode
                                            if ((dictBurnY.Exists(variableIndex) == true)) {
                                                part2 = ("Q" + variableIndex);
                                                // Row name
                                                part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                                // Spaces
                                                Print;
                                                // TODO: # ... Warning!!! not translated
                                                handle;
                                                ("    "
                                                            + (part1
                                                            + (part2 + "1.000")));
                                            }

                                        }

                                    }

                                    // Skip this arc
                                SkipArc:
                                }

                                // Write all the cosntraints that contain Si
                                for (entry : dictSourcesInCell) {
                                    // Get the E-cosntraint name
                                    fromNode = dictNodesMap1.Item[entry];
                                    // Node as alpha-numeric ID
                                    // sourceNode = dictSources.Item(entry)        'Get the node as cell number
                                    // fromNode = dictNodesMap1.Item(sourceNode)   'Node
                                    // Loop through all generators at this 'cell'
                                    stringArray = dictSourcesInCell.Item[entry];
                                    for (g = 1; (g <= UBound(stringArray)); g++) {
                                        // Create the variable name, but just refer to it by its ID
                                        sourceID = long.Parse(stringArray[g]);
                                        for (t = 1; (t <= upperT); t++) {
                                            part1 = ("S"
                                                        + (sourceID.ToString() + t.ToString()));
                                            dictVariables.Add;
                                            part1;
                                            "0";
                                            part1 = (part1 + spaceArray[(10 - part1.Length)]);
                                            // Spaces
                                            // part1 = "S" + CStr(fromNode) + CStr(t)      'Variable name
                                            // part1 = part1 + spaceArray(10 - Len(part1)) 'Spaces
                                            // Row OBJ (only needed if there is a fixed cost to open a source)
                                            // tempCost = dictSourceFixedCost.Item(entry) * discountF(t) * arrayCRF(t)     'Cost coefficient
                                            tempCost = (dictSourceFixedCost.Item[sourceID]
                                                        * (discountF[t] * arrayCRF[t]));
                                            // Cost coefficient
                                            // Stop    'Check OM costs
                                            tempCost = ((dictSourceFixedCost.Item[sourceID]
                                                        * (discountF[t] * arrayCRF[t]))
                                                        + (dictSourceFixedOM.Item[sourceID] * arrayCRF[t]));
                                            Print;
                                            // TODO: # ... Warning!!! not translated
                                            handle;
                                            ("    "
                                                        + (part1 + ("OBJ       " + Format(tempCost, formatText))));
                                            // Print line
                                            // Rows D
                                            for (tau = t; (tau <= upperT); tau++) {
                                                part2 = ("D"
                                                            + (sourceID.ToString() + tau.ToString()));
                                                part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                                // Spaces
                                                sourceCapacity = (dictSourceCapacity.Item[sourceID] * -1);
                                                // Capacity
                                                Print;
                                                // TODO: # ... Warning!!! not translated
                                                handle;
                                                ("    "
                                                            + (part1
                                                            + (part2 + Format(sourceCapacity, formatText))));
                                                // Print the line
                                            }

                                            // Rows H
                                            if ((dictConstraints.Exists(("H" + sourceID.ToString())) == true)) {
                                                part2 = ("H" + sourceID.ToString());
                                                part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                                // Spaces
                                                Print;
                                                // TODO: # ... Warning!!! not translated
                                                handle;
                                                ("    "
                                                            + (part1
                                                            + (part2 + "1.000")));
                                            }

                                            // Rows Q
                                            // Check if Si appears in the fixed solution
                                            if ((dictFixedSolution.Exists(("S" + fromNode.ToString())) == true)) {
                                                itemString = ("S" + fromNode);
                                                // Get entry as string
                                                part2 = ("Q" + dictFixedSolution.Item[itemString].ToString());
                                                part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                                // Spaces
                                                Print;
                                                // TODO: # ... Warning!!! not translated
                                                handle;
                                                ("    "
                                                            + (part1
                                                            + (part2 + "1.000")));
                                            }

                                        }

                                    }

                                }

                                // Write all constraints that contain Wj
                                for (entry : dictSinksInCell) {
                                    fromNode = dictNodesMap1.Item[entry];
                                    // Node as alpha-numeric ID
                                    // Loop through all reservoirs at this 'cell'
                                    stringArray = dictSinksInCell.Item[entry];
                                    for (g = 1; (g <= UBound(stringArray)); g++) {
                                        sinkID = long.Parse(stringArray[g]);
                                        for (t = 1; (t <= upperT); t++) {
                                            part1 = ("W"
                                                        + (sinkID.ToString() + t.ToString()));
                                            dictVariables.Add;
                                            part1;
                                            "0";
                                            part1 = (part1 + spaceArray[(10 - part1.Length)]);
                                            // Spaces
                                            // Row OBJ (only needed if there is a fixed cost to open a reservoir)
                                            // tempCost = dictSinkFixedCostNew.Item(sinkID) * discountF(t) * arrayCRF(t)   'Cost coefficient
                                            // Cost coefficient - fixed and fixed O&M costs
                                            tempCost = ((dictWellFixed.Item[sinkID]
                                                        * (discountF[t] * arrayCRF[t]))
                                                        + (dictWellFixedOM.Item[sinkID] * discountV[t]));
                                            Print;
                                            // TODO: # ... Warning!!! not translated
                                            handle;
                                            ("    "
                                                        + (part1 + ("OBJ       " + Format(tempCost, formatText))));
                                            // Print line
                                            // Rows E
                                            for (tau = t; (tau <= upperT); tau++) {
                                                part2 = ("E"
                                                            + (sinkID.ToString() + tau.ToString()));
                                                part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                                // Spaces
                                                sinkCapacity = (dictWellInjectivity.Item[sinkID] * -1);
                                                // Get capacity
                                                Print;
                                                // TODO: # ... Warning!!! not translated
                                                handle;
                                                ("    "
                                                            + (part1
                                                            + (part2 + Format(sinkCapacity, formatText))));
                                                // Print the line
                                            }

                                            // Rows J
                                            if ((constraint6 == 2)) {
                                                part2 = ("J"
                                                            + (sinkID.ToString() + t.ToString()));
                                                part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                                // Spaces
                                                Print;
                                                // TODO: # ... Warning!!! not translated
                                                handle;
                                                ("    "
                                                            + (part1
                                                            + (part2 + "1.000")));
                                            }

                                            // Rows Q
                                            // Check if Rj appears in the fixed solution
                                            if ((dictFixedSolution.Exists(("R" + fromNode)) == true)) {
                                                itemString = ("R" + fromNode);
                                                // Get entry as string
                                                part2 = ("Q" + dictFixedSolution.Item[itemString].ToString());
                                                part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                                // Spaces
                                                Print;
                                                // TODO: # ... Warning!!! not translated
                                                handle;
                                                ("    "
                                                            + (part1
                                                            + (part2 + "1.000")));
                                            }

                                        }

                                    }

                                }

                                // Write all constraints that contain Rj
                                // For Each entry In dictSinks
                                for (entry : dictSinksInCell) {
                                    // List of sinks at this cell
                                    stringArray = dictSinksInCell.Item[entry];
                                    // Loop through all reservoirs at this 'cell'
                                    for (g = 1; (g <= UBound(stringArray)); g++) {
                                        sinkID = long.Parse(stringArray[g]);
                                        for (t = 1; (t <= upperT); t++) {
                                            part1 = ("R"
                                                        + (sinkID.ToString() + t.ToString()));
                                            dictVariables.Add;
                                            part1;
                                            "0";
                                            part1 = (part1 + spaceArray[(10 - part1.Length)]);
                                            // Spaces
                                            // Row OBJ (only needed if there is a fixed cost to open a reservoir)
                                            // Fixed and fixed O&M costs
                                            tempCost = ((dictSiteFixed.Item[sinkID]
                                                        * (discountF[t] * arrayCRF[t]))
                                                        + (dictSiteFixedOM.Item[sinkID] * discountV[t]));
                                            Print;
                                            // TODO: # ... Warning!!! not translated
                                            handle;
                                            ("    "
                                                        + (part1 + ("OBJ       " + Format(tempCost, formatText))));
                                            // Print line
                                            // Rows J
                                            for (tau = t; (tau <= upperT); tau++) {
                                                part2 = ("J"
                                                            + (sinkID.ToString() + tau.ToString()));
                                                part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                                // Spaces
                                                // Sink capacity (with or without sampling) has already been calculated
                                                if ((constraint6 == 1)) {
                                                    sinkCapacity = (dictCapacityR.Item[sinkID]
                                                                * ((1 * projectLength)
                                                                * -1));
                                                }

                                                // Get reservoir capacity
                                                if ((constraint6 == 2)) {
                                                    sinkCapacity = (dictWellBound.Item[sinkID] * -1);
                                                }

                                                // Get maximum number of wells
                                                Print;
                                                // TODO: # ... Warning!!! not translated
                                                handle;
                                                ("    "
                                                            + (part1
                                                            + (part2 + Format(sinkCapacity, formatText))));
                                                // Print the line
                                            }

                                        }

                                    }

                                }

                                // End of INTEGERS
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handle;
                                "    INTEGER2  \'MARKER\'                 \'INTEND\'";
                                part1 = "P1";
                                part1 = (part1 + spaceArray[(10 - part1.Length)]);
                                // Spaces
                                part2 = "Z1";
                                part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                // Spaces
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handle;
                                ("    "
                                            + (part1
                                            + (part2 + projectLength.ToString())));
                                part1 = "P2";
                                part1 = (part1 + spaceArray[(10 - part1.Length)]);
                                // Spaces
                                part2 = "Z2";
                                part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                // Spaces
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handle;
                                ("    "
                                            + (part1
                                            + (part2 + upperT.ToString())));
                                part1 = "P3";
                                part1 = (part1 + spaceArray[(10 - part1.Length)]);
                                // Spaces
                                part2 = "Z3";
                                part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                // Spaces
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handle;
                                ("    "
                                            + (part1
                                            + (part2 + interest.ToString())));
                                part1 = "P4";
                                part1 = (part1 + spaceArray[(10 - part1.Length)]);
                                // Spaces
                                part2 = "Z4";
                                part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                // Spaces
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handle;
                                ("    "
                                            + (part1
                                            + (part2 + CO2Length.ToString())));
                                for (entry : dictCost) {
                                    stringArray = entry.Split('\t');
                                    // Entry
                                    fromNode = dictNodesMap1.Item[long.Parse(stringArray[0])];
                                    // From node
                                    toNode = dictNodesMap1.Item[long.Parse(stringArray[1])];
                                    // To node node
                                    // Time
                                    for (t = 1; (t <= upperT); t++) {
                                        part1 = ("X"
                                                    + (fromNode + (","
                                                    + (toNode + t.ToString()))));
                                        dictVariables.Add;
                                        part1;
                                        "0";
                                        part1 = (part1 + spaceArray[(10 - part1.Length)]);
                                        // Spaces
                                        // Row OBJ
                                        if ((variablePipe == true)) {
                                            // coefficient = 0.1 * dictLengthCONS.Item(entry) * arrayCRF(t)
                                            coefficient = (pipeArray(4, 1)
                                                        * (discountV[t] * dictLength.Item[entry]));
                                            // If dictLength.Item(entry) > 25 Then Stop
                                            Print;
                                            // TODO: # ... Warning!!! not translated
                                            handle;
                                            ("    "
                                                        + (part1 + ("OBJ       " + Format(coefficient, formatText))));
                                            // Print line
                                        }

                                        // Rows A
                                        // Check consrtaint type
                                        if ((constraint2 == 1)) {
                                            part2 = ("A"
                                                        + (fromNode + (","
                                                        + (toNode + t.ToString()))));
                                            part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                            // Spaces
                                            Print;
                                            // TODO: # ... Warning!!! not translated
                                            handle;
                                            ("    "
                                                        + (part1
                                                        + (part2 + "1.000")));
                                        }
                                        else if ((constraint2 == 2)) {
                                            // Only perform this if i < j
                                            if ((n1 < n2)) {
                                                part2 = ("A"
                                                            + (fromNode + (","
                                                            + (toNode + t.ToString()))));
                                                part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                                // Spaces
                                                Print;
                                                // TODO: # ... Warning!!! not translated
                                                handle;
                                                ("    "
                                                            + (part1
                                                            + (part2 + "1.000")));
                                            }
                                            else {
                                                part2 = ("A"
                                                            + (toNode + (","
                                                            + (fromNode + t.ToString()))));
                                                part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                                // Spaces
                                                Print;
                                                // TODO: # ... Warning!!! not translated
                                                handle;
                                                ("    "
                                                            + (part1
                                                            + (part2 + "1.000")));
                                            }

                                        }

                                        // Rows C (node i-to-j)
                                        part2 = ("C"
                                                    + (fromNode + t.ToString()));
                                        part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                        // Spaces
                                        Print;
                                        // TODO: # ... Warning!!! not translated
                                        handle;
                                        ("    "
                                                    + (part1
                                                    + (part2 + "1.000")));
                                        part2 = ("C"
                                                    + (toNode + t.ToString()));
                                        part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                        // Spaces
                                        Print;
                                        // TODO: # ... Warning!!! not translated
                                        handle;
                                        ("    "
                                                    + (part1
                                                    + (part2 + "-1.000")));
                                    }

                                }

                                // Pijct
                                if ((morbee == true)) {
                                    // Write all constraints that contain Pijc
                                    for (entry : dictCost) {
                                        stringArray = entry.Split('\t');
                                        // Entry
                                        fromNode = dictNodesMap1.Item[long.Parse(stringArray[0])];
                                        // From node
                                        toNode = dictNodesMap1.Item[long.Parse(stringArray[1])];
                                        // To node node
                                        for (t = 1; (t <= upperT); t++) {
                                            for (d = 1; (d <= dBound); d++) {
                                                part1 = ("Q"
                                                            + (fromNode
                                                            + (toNode
                                                            + (d.ToString() + t.ToString()))));
                                                dictVariables.Add;
                                                part1;
                                                "0";
                                                part1 = (part1 + spaceArray[(10 - part1.Length)]);
                                                // Spaces
                                                // Actual node numbers
                                                n1 = long.Parse(stringArray[0]);
                                                n2 = long.Parse(stringArray[1]);
                                                morbeeCON = (arrayMorbee(1, d)
                                                            * (dictLengthCONS.Item[entry] * multiplierCONS));
                                                morbeeROW = (arrayMorbee(3, d)
                                                            * (dictLengthROW.Item[entry] * multiplierROW));
                                                coefficient = ((morbeeCON + morbeeROW)
                                                            * (discountF[t] * arrayCRF[t]));
                                                coefficient = (coefficient * convertToEuros);
                                                // Convert to Euros
                                                if ((utilityAdjust == true)) {
                                                    coefficient = (coefficient / pipeUtilization);
                                                }

                                                // morbeeLength = (dictLengthROW.Item(entry) + (3 * dictLengthCONS.Item(entry))) / 4
                                                // coefficient = arrayMorbee(1, d) * morbeeLength * multiplierROW * crf  'Amortized  cost
                                                Print;
                                                // TODO: # ... Warning!!! not translated
                                                handle;
                                                ("    "
                                                            + (part1 + ("OBJ       " + Format(coefficient, formatText))));
                                                // Print line
                                                // Rows A
                                                // Check constraint type
                                                tempCapacity = (0 - pipeUtilization);
                                                if ((utilityAdjust == true)) {
                                                    tempCapacity = -1;
                                                }

                                                // Stop
                                                for (tau = t; (tau <= upperT); tau++) {
                                                    if ((constraint2 == 1)) {
                                                        part2 = ("A"
                                                                    + (fromNode + (","
                                                                    + (toNode + tau.ToString()))));
                                                        part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                                        // Spaces
                                                        // Print #handle, "    " + part1 + part2 + "-1.000"                       'Print line
                                                        Print;
                                                        // TODO: # ... Warning!!! not translated
                                                        handle;
                                                        ("    "
                                                                    + (part1
                                                                    + (part2 + Format(tempCapacity, formatText))));
                                                        // Print line
                                                    }
                                                    else if ((constraint2 == 2)) {
                                                        // Only perform this if i < j
                                                        if ((n1 < n2)) {
                                                            // Normal
                                                            part2 = ("A"
                                                                        + (fromNode + (","
                                                                        + (toNode + tau.ToString()))));
                                                            part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                                            // Spaces
                                                            Print;
                                                            // TODO: # ... Warning!!! not translated
                                                            handle;
                                                            ("    "
                                                                        + (part1
                                                                        + (part2 + Format(tempCapacity, formatText))));
                                                            // Print line
                                                        }
                                                        else {
                                                            // Reversed
                                                            part2 = ("A"
                                                                        + (toNode + (","
                                                                        + (fromNode + tau.ToString()))));
                                                            part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                                            // Spaces
                                                            Print;
                                                            // TODO: # ... Warning!!! not translated
                                                            handle;
                                                            ("    "
                                                                        + (part1
                                                                        + (part2 + Format(tempCapacity, formatText))));
                                                            // Print line
                                                        }

                                                    }

                                                }

                                                // Rows K
                                                part2 = ("K"
                                                            + (fromNode
                                                            + (toNode
                                                            + (d.ToString() + t.ToString()))));
                                                part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                                // Spaces
                                                Print;
                                                // TODO: # ... Warning!!! not translated
                                                handle;
                                                ("    "
                                                            + (part1
                                                            + (part2 + "-1.000")));
                                                itemString = ("Q"
                                                            + (fromNode
                                                            + (toNode + d.ToString())));
                                                if ((dictGlobalBurn.Exists(itemString) == true)) {
                                                    // SHould not be value of 1!!!
                                                    part1 = (itemString + spaceArray[(10 - itemString.Length)]);
                                                    // Spaces
                                                    part2 = ("Q" + itemString);
                                                    // Row name
                                                    part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                                    // Spaces
                                                    Print;
                                                    // TODO: # ... Warning!!! not translated
                                                    handle;
                                                    ("    "
                                                                + (part1
                                                                + (part2 + "1.000")));
                                                }

                                            }

                                        }

                                    }

                                }

                                // All constraints that contain variable Ai
                                for (entry : dictSourcesInCell) {
                                    fromNode = dictNodesMap1.Item[entry];
                                    // Node as alpha-numeric ID
                                    // Stop
                                    // Loop through all generators at this 'cell'
                                    stringArray = dictSourcesInCell.Item[entry];
                                    for (g = 1; (g <= UBound(stringArray)); g++) {
                                        // Create the variable name, but just refer to it by its ID
                                        sourceID = long.Parse(stringArray[g]);
                                        for (t = 1; (t <= upperT); t++) {
                                            // part1 = "A" + fromNode + CStr(t)            'Variable name
                                            // part1 = part1 + spaceArray(10 - Len(part1)) 'Spaces
                                            part1 = ("A"
                                                        + (sourceID.ToString() + t.ToString()));
                                            dictVariables.Add;
                                            part1;
                                            "0";
                                            part1 = (part1 + spaceArray[(10 - part1.Length)]);
                                            // Spaces
                                            // Row OBJ
                                            if ((simCCStax == true)) {
                                                // Row OBJ
                                                // Variable cost and CO2 tax
                                                if ((variableSource == true)) {
                                                    tempCost = (dictSourceVariableOM.Item[sourceID] - timeArray(4, t));
                                                    // Minus CO2 tax from variable capture cost
                                                    tempCost = (tempCost * discountV[t]);
                                                    Print;
                                                    // TODO: # ... Warning!!! not translated
                                                    handle;
                                                    ("    "
                                                                + (part1 + ("OBJ       " + Format(tempCost, formatText))));
                                                    // Print line
                                                }
                                                else {
                                                    tempCost = (0 - targetCO2);
                                                    // Minus CO2 tax from variable capture cost
                                                    Print;
                                                    // TODO: # ... Warning!!! not translated
                                                    handle;
                                                    ("    "
                                                                + (part1 + ("OBJ       " + Format(tempCost, formatText))));
                                                    // Print line
                                                }

                                            }
                                            else {
                                                // Check if there is a credit to capture CO2
                                                if ((variableSource == true)) {
                                                    if ((creditSource == true)) {
                                                        tempCost = (dictSourceVariableOM.Item[sourceID] - dictSourceCredit.Item[sourceID]);
                                                    }

                                                    // Cost
                                                    if ((creditSource == false)) {
                                                        tempCost = dictSourceVariableOM.Item[sourceID];
                                                    }

                                                    // Cost
                                                    tempCost = (tempCost * discountV[t]);
                                                    // Discount
                                                    Print;
                                                    // TODO: # ... Warning!!! not translated
                                                    handle;
                                                    ("    "
                                                                + (part1 + ("OBJ       " + Format(tempCost, formatText))));
                                                    // Print line
                                                }
                                                else if ((creditSource == true)) {
                                                    tempCost = (0 - dictSourceCredit.Item[sourceID]);
                                                    // Cost
                                                    tempCost = (tempCost * discountV[t]);
                                                    // Discount
                                                    Print;
                                                    // TODO: # ... Warning!!! not translated
                                                    handle;
                                                    ("    "
                                                                + (part1 + ("OBJ       " + Format(tempCost, formatText))));
                                                    // Print line
                                                }

                                            }

                                            // Rows C
                                            part2 = ("C"
                                                        + (fromNode + t.ToString()));
                                            part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                            // Spaces
                                            Print;
                                            // TODO: # ... Warning!!! not translated
                                            handle;
                                            ("    "
                                                        + (part1
                                                        + (part2 + "-1.000")));
                                            part2 = ("D"
                                                        + (sourceID.ToString() + t.ToString()));
                                            part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                            // Spaces
                                            Print;
                                            // TODO: # ... Warning!!! not translated
                                            handle;
                                            ("    "
                                                        + (part1
                                                        + (part2 + "1.000")));
                                            if ((simCCStax == false)) {
                                                part2 = ("F" + t.ToString());
                                                part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                                // Spaces
                                                Print;
                                                // TODO: # ... Warning!!! not translated
                                                handle;
                                                ("    "
                                                            + (part1
                                                            + (part2 + "1.000")));
                                            }

                                        }

                                    }

                                }

                                // All constraints that contain Bj
                                for (entry : dictSinksInCell) {
                                    fromNode = dictNodesMap1.Item[entry];
                                    // Node as alpha-numeric ID
                                    // Loop through all generators at this 'cell'
                                    stringArray = dictSinksInCell.Item[entry];
                                    for (g = 1; (g <= UBound(stringArray)); g++) {
                                        sinkID = long.Parse(stringArray[g]);
                                        for (t = 1; (t <= upperT); t++) {
                                            // part1 = "B" + fromNode + CStr(t)            'Variable name
                                            // part1 = part1 + spaceArray(10 - Len(part1)) 'Spaces
                                            part1 = ("B"
                                                        + (sinkID.ToString() + t.ToString()));
                                            dictVariables.Add;
                                            part1;
                                            "0";
                                            part1 = (part1 + spaceArray[(10 - part1.Length)]);
                                            // Spaces
                                            // Row OBJ
                                            if ((variableSink == true)) {
                                                sinkCost = dictWellVariableOM.Item[sinkID];
                                                // Get sink cost
                                                if ((creditSink == true)) {
                                                    sinkCost = (sinkCost - dictSinkCreditNew.Item[sinkID]);
                                                }

                                                // Adjust for storage credit
                                                sinkCost = (sinkCost * discountV[t]);
                                                // Discount
                                                Print;
                                                // TODO: # ... Warning!!! not translated
                                                handle;
                                                ("    "
                                                            + (part1 + ("OBJ       " + Format(sinkCost, formatText))));
                                                // Print line
                                            }

                                            // Rows C
                                            part2 = ("C"
                                                        + (fromNode + t.ToString()));
                                            part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                            // Spaces
                                            Print;
                                            // TODO: # ... Warning!!! not translated
                                            handle;
                                            ("    "
                                                        + (part1
                                                        + (part2 + "1.000")));
                                            part2 = ("E"
                                                        + (sinkID.ToString() + t.ToString()));
                                            part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                            // Spaces
                                            tempCapacity = timeArray(3, t);
                                            // Number of years in this time period
                                            Print;
                                            // TODO: # ... Warning!!! not translated
                                            handle;
                                            ("    "
                                                        + (part1
                                                        + (part2 + "1.000")));
                                            if ((dictConstraints.Exists(("I" + sinkID.ToString())) == true)) {
                                                part2 = ("I" + sinkID.ToString());
                                                part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                                // Spaces
                                                tempCapacity = timeArray(2, t);
                                                // Number of years in this time period
                                                Print;
                                                // TODO: # ... Warning!!! not translated
                                                handle;
                                                ("    "
                                                            + (part1
                                                            + (part2 + Format(tempCapacity, formatText))));
                                                // Print line
                                            }

                                            // Rows J
                                            if ((constraint6 == 1)) {
                                                part2 = ("J"
                                                            + (sinkID.ToString() + t.ToString()));
                                                part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                                // Spaces
                                                Print;
                                                // TODO: # ... Warning!!! not translated
                                                handle;
                                                ("    "
                                                            + (part1
                                                            + (part2 + "1.000")));
                                            }

                                        }

                                    }

                                }

                                // Write the constraint to store amount for NOT capturing any CO2
                                if ((simCCStax == true)) {
                                    // part1 = "P4"                                    'Variable name
                                    // part1 = part1 + spaceArray(10 - Len(part1))     'Spaces
                                    // part2 = "T"                                     'Row name
                                    // part2 = part2 + spaceArray(10 - Len(part2))     'Spaces
                                    // Print #handle, "    " + part1 + part2 + CStr(1) 'Print the line
                                    // 'The variable also goes in the objcetive function
                                    // part2 = "OBJ"                                     'Row name
                                    // part2 = part2 + spaceArray(10 - Len(part2))     'Spaces
                                    // Print #handle, "    " + part1 + part2 + CStr(1) 'Print the line
                                }

                                // ''''''''''''''''''
                                // '' RHS SECTION '''
                                // ''''''''''''''''''
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handle;
                                "RHS";
                                if ((fixedSource == false)) {
                                    for (entry : dictSources) {
                                        for (t = 1; (t <= upperT); t++) {
                                            sourceNode = dictSources.Item[entry];
                                            // Get the node as cell number
                                            fromNode = dictNodesMap1.Item[sourceNode];
                                            // Node
                                            part1 = "    RHS       ";
                                            part2 = ("D"
                                                        + (fromNode + t.ToString()));
                                            part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                            // Spaces
                                            tempCapacity = dictSourceCapacity.Item[entry];
                                            // Annual supply
                                            Print;
                                            // TODO: # ... Warning!!! not translated
                                            handle;
                                            (part1
                                                        + (part2 + tempCapacity.ToString()));
                                        }

                                    }

                                }

                                // Rows E
                                // Defaults to 0 if Rj is in the model
                                if ((fixedSink == false)) {
                                    for (entry : dictSinks) {
                                        for (t = 1; (t <= upperT); t++) {
                                            sinkNode = dictSinks.Item[entry];
                                            // Get the node as cell number
                                            fromNode = dictNodesMap1.Item[sinkNode];
                                            // Node
                                            part1 = "    RHS       ";
                                            part2 = ("E"
                                                        + (fromNode + t.ToString()));
                                            part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                            // Spaces
                                            sinkCapacity = dictSiteCapacity.Item[entry];
                                            // Get capacity
                                            Print;
                                            // TODO: # ... Warning!!! not translated
                                            handle;
                                            (part1
                                                        + (part2 + sinkCapacity.ToString()));
                                        }

                                    }

                                }

                                // This constraint is NOT needed for SimCCS-tax
                                if ((simCCStax == false)) {
                                    // Row F
                                    // Set to value for each time period t
                                    for (t = 1; (t <= upperT); t++) {
                                        Print;
                                        // TODO: # ... Warning!!! not translated
                                        handle;
                                        ("    RHS       F"
                                                    + (t.ToString() + ("        " + Format(timeArray(4, t), formatText))));
                                    }

                                }

                                // 'Set constrain T equal to total cost of NOT capturring CO2
                                // If simCCStax = True Then
                                //     'Row F
                                //     Stop
                                //     For t = 1 To upperT
                                //         Print #handle, "    RHS       T" + CStr(t) + "        " + Format(totalCO2 * targetCO2, formatText)
                                //     Next t
                                // End If
                                // Rows G
                                if ((onePipeOnly == true)) {
                                    for (entry : dictCost) {
                                        stringArray = entry.Split('\t');
                                        // Entry
                                        fromNode = dictNodesMap1.Item[long.Parse(stringArray[0])];
                                        // From node
                                        toNode = dictNodesMap1.Item[long.Parse(stringArray[1])];
                                        // To node node
                                        // Loop through time periods
                                        for (t = 1; (t <= upperT); t++) {
                                            part1 = "    RHS       ";
                                            part2 = ("G "
                                                        + (fromNode + (", "
                                                        + (toNode + t.ToString()))));
                                            part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                            // Spaces
                                            Print;
                                            // TODO: # ... Warning!!! not translated
                                            handle;
                                            (part1
                                                        + (part2 + "1.000"));
                                            Print;
                                            // TODO: # ... Warning!!! not translated
                                            handle;
                                            (" L  G"
                                                        + (fromNode + (","
                                                        + (toNode + t.ToString()))));
                                        }

                                    }

                                }

                                // Rows H
                                for (entry : dictSources) {
                                    // sourceNode = dictSources.Item(entry)                    'Get the node as cell number
                                    // fromNode = dictNodesMap1.Item(sourceNode)               'Node
                                    part1 = "    RHS       ";
                                    part2 = ("H" + entry.ToString());
                                    part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                    // Spaces
                                    nGens = dictGenerators.Item[entry];
                                    // Number of generators
                                    // Print #handle, part1 + part2 + "1.000"                  'Print line
                                    if ((dictConstraints.Exists(("H" + sourceID.ToString())) == true)) {
                                        Print;
                                    }

                                    // TODO: # ... Warning!!! not translated
                                    handle;
                                    (part1
                                                + (part2 + Format(nGens, formatText)));
                                    // Print line
                                    // Print #handle, part1 + part2 + Format(nGens, formatText)   'Print line
                                }

                                // Rows I
                                for (entry : dictSinks) {
                                    // sinkNode = dictSinks.Item(entry)                    'Get the node as cell number
                                    // fromNode = dictNodesMap1.Item(sinkNode)             'Node
                                    part1 = "    RHS       ";
                                    part2 = ("I" + entry.ToString());
                                    part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                    // Spaces
                                    sinkCapacity = dictSiteCapacity.Item[entry];
                                    // Get capacity
                                    if ((dictConstraints.Exists(("I" + entry.ToString())) == true)) {
                                        Print;
                                    }

                                    // TODO: # ... Warning!!! not translated
                                    handle;
                                    (part1
                                                + (part2 + sinkCapacity.ToString()));
                                }

                                // Rows M (only needed for Morbee)
                                if (((morbee == true)
                                            && (useM == true))) {
                                    for (entry : dictCost) {
                                        for (t = 1; (t <= upperT); t++) {
                                            stringArray = entry.Split('\t');
                                            // Entry
                                            fromNode = dictNodesMap1.Item[long.Parse(stringArray[0])];
                                            // From node
                                            toNode = dictNodesMap1.Item[long.Parse(stringArray[1])];
                                            // To node node
                                            part1 = "    RHS       ";
                                            part2 = ("M"
                                                        + (fromNode + (","
                                                        + (toNode + t.ToString()))));
                                            part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                            // Spaces
                                            Print;
                                            // TODO: # ... Warning!!! not translated
                                            handle;
                                            (part1
                                                        + (part2 + "1.000"));
                                        }

                                    }

                                }

                                // Rows Q (fixed-in solution)
                                for (entry : dictBurnY) {
                                    part1 = "    RHS       ";
                                    part2 = ("Q" + entry);
                                    // Row name
                                    part2 = (part2 + spaceArray[(13 - part2.Length)]);
                                    // Spaces
                                    Print;
                                    // TODO: # ... Warning!!! not translated
                                    handle;
                                    (part1
                                                + (part2 + "1.000"));
                                }

                                // Rows Z
                                // Defaults to 0
                                // '''''''''''''''''''''
                                // '' BOUNDS SECTION '''
                                // '''''''''''''''''''''
                                // Check if we need to write the bounds (integer variables)
                                // Write bounds for all integer variables
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handle;
                                "BOUNDS";
                                for (entry : dictSourcesInCell) {
                                    // Loop through all generators at this 'cell'
                                    stringArray = dictSourcesInCell.Item[entry];
                                    for (t = 1; (t <= upperT); t++) {
                                        for (g = 1; (g <= UBound(stringArray)); g++) {
                                            // Write the [upper] bound
                                            sourceID = long.Parse(stringArray[g]);
                                            part2 = ("S"
                                                        + (sourceID.ToString() + t.ToString()));
                                            nGens = dictGenerators.Item[sourceID];
                                            // Number of generators
                                            if ((nGens > 1)) {
                                                part1 = " UP BND1      ";
                                                part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                                // Spaces
                                                Print;
                                                // TODO: # ... Warning!!! not translated
                                                handle;
                                                (part1
                                                            + (part2 + Format(nGens, formatText)));
                                                // Print line
                                            }
                                            else if ((nGens == 1)) {
                                                part1 = " BV BND1      ";
                                                part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                                // Spaces
                                                Print;
                                                // TODO: # ... Warning!!! not translated
                                                handle;
                                                (part1
                                                            + (part2 + "1.000"));
                                            }
                                            else {

                                            }

                                        }

                                    }

                                }

                                //     'Rj
                                //     For Each entry In dictSinks
                                //         'If there's no cost for Rj, do NOT write any constraints that contain Rj
                                //         If fixedSink = True Then
                                //             'Calculate variable name
                                //             sinkNode = dictSinks.Item(entry)            'Get the node as cell number
                                //             fromNode = dictNodesMap1.Item(sinkNode)     'Node
                                //
                                //             'Write the [upper] bound
                                //             For t = 1 To upperT
                                //                 part1 = " UP BND1      "                    'Upper bound
                                //                 part2 = "R" + CStr(fromNode) + CStr(t)      'Variable name
                                //                 part2 = part2 + spaceArray(10 - Len(part2)) 'Spaces
                                //                 Print #handle, part1 + part2 + "1.000"      'Print line
                                //                 Next t
                                //         End If
                                //     Next
                                // Write bounds for wells
                                for (entry : dictSinksInCell) {
                                    // Get the E-cosntraint name
                                    fromNode = dictNodesMap1.Item[entry];
                                    // Node as alpha-numeric ID
                                    // Loop through all generators at this 'cell'
                                    stringArray = dictSinksInCell.Item[entry];
                                    // Write the [upper] bound
                                    for (t = 1; (t <= upperT); t++) {
                                        for (g = 1; (g <= UBound(stringArray)); g++) {
                                            // Write the [upper] bound
                                            sinkID = long.Parse(stringArray[g]);
                                            part1 = " UP BND1      ";
                                            part2 = ("W"
                                                        + (sinkID.ToString() + t.ToString()));
                                            part2 = (part2 + spaceArray[(10 - part2.Length)]);
                                            // Spaces
                                            nWells = dictWellBound.Item[sinkID];
                                            Print;
                                            // TODO: # ... Warning!!! not translated
                                            handle;
                                            (part1
                                                        + (part2 + nWells.ToString()));
                                        }

                                    }

                                }

                                // Z1, Z2, Z3
                                // Write the [upper] bound
                                part1 = " UP BND1      ";
                                part2 = ("P1" + spaceArray[(10 - "P1".Length)]);
                                // Spaces
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handle;
                                (part1
                                            + (part2 + projectLength.ToString()));
                                part1 = " UP BND1      ";
                                part2 = ("P2" + spaceArray[(10 - "P2".Length)]);
                                // Spaces
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handle;
                                (part1
                                            + (part2 + upperT.ToString()));
                                part1 = " UP BND1      ";
                                part2 = ("P3" + spaceArray[(10 - "P3".Length)]);
                                // Spaces
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handle;
                                (part1
                                            + (part2 + interest.ToString()));
                                part1 = " UP BND1      ";
                                part2 = ("P4" + spaceArray[(10 - "P4".Length)]);
                                // Spaces
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handle;
                                (part1
                                            + (part2 + CO2Length.ToString()));
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handle;
                                "ENDATA";
                                Close;
                                // TODO: # ... Warning!!! not translated
                                handle;
                                // Write a CPLEX batch file
                                // Create log file name
                                object stringArray;
                                for (t = 1; (t <= upperT); t++) {
                                    stringArray[(t - 1)] = (timeArray(2, t).ToString() + ("-" + timeArray(4, t).ToString()));
                                }

                                stringArray[upperT] = Format(interest, "#0.######");
                                stringArray[(upperT + 1)] = discountRate.ToString();
                                object fs;
                                boolean useStart;
                                if (!(Dir((savePath + ("time-"
                                                + (Join(stringArray, "-") + ".txt")))) == "")) {
                                    fs = new FileSystemObject();
                                    fs.DeleteFile;
                                    (savePath + ("time-"
                                                + (Join(stringArray, "-") + ".txt")));
                                    DoEvents;
                                }

                                Print;
                                // TODO: # ... Warning!!! not translated
                                handleMPS;
                                ("set log time-"
                                            + (Join(stringArray, "-") + ".txt"));
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handleMPS;
                                "read time.mps";
                                if (!(Dir((savePath
                                                + (mipName + ".sol"))) == "")) {
                                    fs = new FileSystemObject();
                                    fs.DeleteFile;
                                    (savePath
                                                + (mipName + ".sol"));
                                    DoEvents;
                                }

                                if ((startingSolution == true)) {
                                    // Write the command and create the file
                                    // Delete sol file (so CPLEX does not ask whether to overwrite it or not)
                                    if ((CreateStartingSolution((savePath + ("Previous Solutions\\" + "time.sol")), (savePath + "time.sol"), (savePath + ("Previous Solutions\\" + ("time-"
                                                    + (Join(stringArray, "-") + ".start"))))) == true)) {
                                        Print;
                                        // TODO: # ... Warning!!! not translated
                                        handleMPS;
                                        ("read "
                                                    + (mipName + ".sol"));
                                    }
                                    else {
                                        MsgBox;
                                        "Could not read starting solution";
                                        vbCritical;
                                        "Error";
                                        useStart = false;
                                    }

                                }

                                // Print constraints (OBJ, A, D, and E)
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handleMPS;
                                "display problem constraints OBJ";
                                for (entry : dictCost) {
                                    stringArray = entry.Split('\t');
                                    // Entry
                                    fromNode = dictNodesMap1.Item[long.Parse(stringArray[0])];
                                    // From node
                                    toNode = dictNodesMap1.Item[long.Parse(stringArray[1])];
                                    // To node node
                                    n1 = long.Parse(stringArray[0]);
                                    n2 = long.Parse(stringArray[1]);
                                    for (t = 1; (t <= upperT); t++) {
                                        // Print constraint
                                        if ((constraint2 == 1)) {
                                            Print;
                                            // TODO: # ... Warning!!! not translated
                                            handleMPS;
                                            ("display problem constraints " + ("A"
                                                        + (fromNode + (","
                                                        + (toNode + t.ToString())))));
                                        }
                                        else if ((constraint2 == 2)) {
                                            if ((n1 < n2)) {
                                                Print;
                                            }

                                            // TODO: # ... Warning!!! not translated
                                            handleMPS;
                                            ("display problem constraints " + ("A"
                                                        + (fromNode + (","
                                                        + (toNode + t.ToString())))));
                                        }

                                    }

                                }

                                // Print C constraints
                                for (entry : dictNodes) {
                                    fromNode = dictNodesMap1.Item[entry];
                                    // Node
                                    for (t = 1; (t <= upperT); t++) {
                                        Print;
                                        // TODO: # ... Warning!!! not translated
                                        handleMPS;
                                        ("display problem constraints " + ("C"
                                                    + (fromNode + t.ToString())));
                                    }

                                }

                                // Print D constraints
                                for (entry : dictSources) {
                                    for (t = 1; (t <= upperT); t++) {
                                        Print;
                                        // TODO: # ... Warning!!! not translated
                                        handleMPS;
                                        ("display problem constraints " + ("D"
                                                    + (entry.ToString() + t.ToString())));
                                    }

                                }

                                // Print E constraints
                                for (entry : dictSinks) {
                                    for (t = 1; (t <= upperT); t++) {
                                        Print;
                                        // TODO: # ... Warning!!! not translated
                                        handleMPS;
                                        ("display problem constraints " + ("E"
                                                    + (entry.ToString() + t.ToString())));
                                    }

                                }

                                // Print F constraints
                                if ((simCCStax == false)) {
                                    for (t = 1; (t <= upperT); t++) {
                                        Print;
                                        // TODO: # ... Warning!!! not translated
                                        handleMPS;
                                        ("display problem constraints " + ("F" + t.ToString()));
                                    }

                                }

                                // Print I constraints
                                for (entry : dictSinks) {
                                    Print;
                                    // TODO: # ... Warning!!! not translated
                                    handleMPS;
                                    ("display problem constraints " + ("I" + entry.ToString()));
                                }

                                // Rows J
                                for (entry : dictSinks) {
                                    // Loop through time periods
                                    for (t = 1; (t <= upperT); t++) {
                                        Print;
                                        // TODO: # ... Warning!!! not translated
                                        handleMPS;
                                        ("display problem constraints " + ("J"
                                                    + (entry.ToString() + t.ToString())));
                                    }

                                }

                                // Print the Si bounds
                                for (entry : dictSources) {
                                    for (t = 1; (t <= upperT); t++) {
                                        Print;
                                        // TODO: # ... Warning!!! not translated
                                        handleMPS;
                                        ("display problem bounds " + ("S"
                                                    + (entry.ToString() + t.ToString())));
                                    }

                                }

                                // Print the Wj bounds
                                for (entry : dictSinks) {
                                    for (t = 1; (t <= upperT); t++) {
                                        Print;
                                        // TODO: # ... Warning!!! not translated
                                        handleMPS;
                                        ("display problem bounds " + ("W"
                                                    + (entry.ToString() + t.ToString())));
                                    }

                                }

                                Print;
                                // TODO: # ... Warning!!! not translated
                                handleMPS;
                                ("set mip tol mipgap " + gap.ToString());
                                if ((useStart == true)) {
                                    Print;
                                }

                                // TODO: # ... Warning!!! not translated
                                handleMPS;
                                "set emp mip 0";
                                if ((useStart == false)) {
                                    Print;
                                }

                                // TODO: # ... Warning!!! not translated
                                handleMPS;
                                "set emp mip 2";
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handleMPS;
                                ("set timelimit " + timeLimit.ToString());
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handleMPS;
                                "set mip str file 3";
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handleMPS;
                                "set mip str node 2";
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handleMPS;
                                "set workmem 1024";
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handleMPS;
                                ("set threads " + nCPUs.ToString());
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handleMPS;
                                "mip";
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handleMPS;
                                "dis sol var -";
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handleMPS;
                                ("write "
                                            + (mipName + ".sol"));
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handleMPS;
                                "y";
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handleMPS;
                                ("dis pro var " + "P1");
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handleMPS;
                                ("dis pro var " + "P2");
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handleMPS;
                                ("dis pro var " + "P3");
                                Print;
                                // TODO: # ... Warning!!! not translated
                                handleMPS;
                                ("dis pro var " + "P4");
                            }

                            // Create a starting solution file
                            ((boolean)(CreateStartingSolution(((string)(openName)), ((string)(saveName)), ((string)(startingName)))));
                            // On Error GoTo err
                            CreateStartingSolution = false;
                            int openSOL;
                            int saveSOL;
                            int openSTART;
                            long lineCount;
                            string conName;
                            string indexName;
                            string slackName;
                            Dictionary dictStartSolution;
                            string vName;
                            float vValue;
                            string dName;
                            string tName;
                            long pRow;
                            string xName;
                            string yName;
                            float xValue;
                            float yValue;
                            float entry2;
                            long cell1;
                            long cell2;
                            string[] listOfArcs1;
                            string[] listOfArcs2;
                            string[] listOfArcs3;
                            long a;
                            long b;
                            long c;
                            string arc1;
                            string arc2;
                            string test1;
                            string test2;
                            string[] tempStringArray;
                            Dictionary dictVariablesY;
                            Dictionary dictVariablesX;
                            string fNode;
                            string tNode;
                            string pID;
                            boolean usingY;
                            float xFlow;
                            string valueString;
                            Dictionary dictCheckV;
                            dictCheckV = new Dictionary();
                            if ((Dir(openName) == "")) {
                                // TODO: Exit Function: Warning!!! Need to return the value
                            }

                            return;
                            if ((Dir(startingName) == "")) {
                                // TODO: Exit Function: Warning!!! Need to return the value
                            }

                            return;
                            // Open BOTH files
                            openSOL = FreeFile;
                            Open;
                            openName;
                            for (object Input; ; Input++) {
                                // TODO: # ... Warning!!! not translated
                                openSOL;
                                saveSOL = FreeFile;
                                Open;
                                saveName;
                                for (object Output; ; Output++) {
                                    // TODO: # ... Warning!!! not translated
                                    saveSOL;
                                    openSTART = FreeFile;
                                    Open;
                                    startingName;
                                    for (object Input; ; Input++) {
                                        // TODO: # ... Warning!!! not translated
                                        openSTART;
                                        // Read through the existing solution file and write all header information to the new solution file
                                        while (!EOF(openSOL)) {
                                            Line;
                                            Input;
                                            // TODO: # ... Warning!!! not translated
                                            openSOL;
                                            readString;
                                            if ((readString == " <linearConstraints>")) {
                                                // We are ready to CLOSE this file and write the rest of the new solution file ourselfves from scratch
                                                Print;
                                                // TODO: # ... Warning!!! not translated
                                                saveSOL;
                                                readString;
                                                Close;
                                                // TODO: # ... Warning!!! not translated
                                                openSOL;
                                                break; //Warning!!! Review that break works as 'Exit Do' as it could be in a nested instruction like switch
                                            }
                                            else {
                                                // Write the line to the SAVE file
                                                Print;
                                                // TODO: # ... Warning!!! not translated
                                                saveSOL;
                                                readString;
                                            }

                                        }

                                        // Starting writing the constraints
                                        lineCount = -1;
                                        // Loop through all entries in dictCosntraints
                                        for (entry : dictConstraints) {
                                            // get constraint and index name
                                            lineCount = (lineCount + 1);
                                            conName = ('\"'
                                                        + (entry + '\"'));
                                            indexName = ('\"'
                                                        + (lineCount.ToString() + '\"'));
                                            slackName = ('\"' + ("0" + '\"'));
                                            // Print cosnrtaint with 0 slack (we have NO idea what the slack should be for this solution because consrtaints could be different
                                            Print;
                                            // TODO: # ... Warning!!! not translated
                                            saveSOL;
                                            ("  <constraint name="
                                                        + (conName + (" index="
                                                        + (indexName + (" slack="
                                                        + (slackName + "/>"))))));
                                        }

                                        // Start writing out the variables
                                        Print;
                                        // TODO: # ... Warning!!! not translated
                                        saveSOL;
                                        " </linearConstraints>";
                                        Print;
                                        // TODO: # ... Warning!!! not translated
                                        saveSOL;
                                        " <variables>";
                                        dictStartSolution = new Dictionary();
                                        Line;
                                        Input;
                                        // TODO: # ... Warning!!! not translated
                                        openSTART;
                                        readString;
                                        // Header
                                        // Read until we reach the y variables
                                        for (
                                        ; true;
                                        ) {
                                            Line;
                                            Input;
                                            // TODO: # ... Warning!!! not translated
                                            openSTART;
                                            readString;
                                            // Read line
                                            stringArray = readString.Split('\t');
                                            // Split text
                                            vName = stringArray[0];
                                            if ((vName == "yID")) {
                                                break; //Warning!!! Review that break works as 'Exit Do' as it could be in a nested instruction like switch
                                            }

                                            vValue = float.Parse(stringArray[1]);
                                            if ((dictVariables.Exists(vName) == true)) {
                                                dictVariables.Item[vName] = vValue.ToString();
                                            }
                                            else {

                                            }

                                        }

                                        usingY = true;
                                        dictVariablesY = new Dictionary();
                                        dictVariablesX = new Dictionary();
                                        while (!EOF(openSTART)) {
                                            // Read in first line
                                            Line;
                                            Input;
                                            // TODO: # ... Warning!!! not translated
                                            openSTART;
                                            readString;
                                            // Read line
                                            stringArray = readString.Split('\t');
                                            // Split text
                                            if ((stringArray[0] == "Flow")) {
                                                usingY = false;
                                                Line;
                                                Input;
                                                // TODO: # ... Warning!!! not translated
                                                openSTART;
                                                readString;
                                                // Read line
                                                stringArray = readString.Split('\t');
                                                // Split text
                                            }

                                            if ((usingY == true)) {
                                                dName = stringArray[1];
                                                // Pipeline diamter
                                                tName = stringArray[2];
                                                // Time period
                                                // Find the ID that corresponds with the ID
                                                pRow = 0;
                                                for (entry2 : dictTransName) {
                                                    if ((dictTransName.Item[entry2] == dName)) {
                                                        pRow = entry2;
                                                        break;
                                                    }

                                                }

                                                if ((pRow == 0)) {

                                                }

                                            }
                                            else {
                                                xFlow = float.Parse(stringArray[0]);
                                                tName = stringArray[1];
                                                // Time period
                                            }

                                            // Read in the cells
                                            Line;
                                            Input;
                                            // TODO: # ... Warning!!! not translated
                                            openSTART;
                                            readString;
                                            // Read line
                                            stringArray = readString.Split('\t');
                                            // Split text
                                            arc1 = "";
                                            for (a = 1; (a
                                                        <= (UBound(stringArray) - 1)); a++) {
                                                // Get cells
                                                // cell1 = CLng(stringArray(1))        'First cell
                                                // cell2 = CLng(stringArray(2))        'Second cell
                                                cell1 = long.Parse(stringArray[a]);
                                                cell2 = long.Parse(stringArray[(a + 1)]);
                                                listOfArcs1 = dictArcs.Item[cell1];
                                                // List of cells
                                                listOfArcs2 = dictArcs.Item[cell2];
                                                // List of cells
                                                // Loop through all possible arcs
                                                object listOfArcs3;
                                                for (b = 1; (b <= UBound(listOfArcs1)); b++) {
                                                    for (c = 1; (c <= UBound(listOfArcs2)); c++) {
                                                        // Check if the two cells share an arc
                                                        if ((listOfArcs1[b] == listOfArcs2[c])) {
                                                            // Expand and store arcs
                                                            object Preserve;
                                                            listOfArcs3[(UBound(listOfArcs3) + 1)];
                                                            listOfArcs3[UBound(listOfArcs3)] = listOfArcs1[b];
                                                        }

                                                    }

                                                }

                                                if ((UBound(listOfArcs3) == 0)) {

                                                }

                                                if ((UBound(listOfArcs3) != 2)) {

                                                }

                                                // Check if the two arcs are different from arc1
                                                if (((listOfArcs3[1] != arc1)
                                                            && (listOfArcs3[2] != arc1))) {
                                                    // Check which direction of arc we claim we are using!
                                                    tempStringArray = listOfArcs3[1].Split('\t');
                                                    // Check
                                                    if ((long.Parse(tempStringArray[0]) == cell1)) {
                                                        arc1 = listOfArcs3[1];
                                                        fNode = dictNodesMap1.Item[long.Parse(tempStringArray[0])];
                                                        tNode = dictNodesMap1.Item[long.Parse(tempStringArray[1])];
                                                        // Stop
                                                    }
                                                    else {
                                                        arc1 = listOfArcs3[2];
                                                        fNode = dictNodesMap1.Item[long.Parse(tempStringArray[1])];
                                                        tNode = dictNodesMap1.Item[long.Parse(tempStringArray[0])];
                                                        // Stop
                                                    }

                                                    // Store y and x variables
                                                    if ((usingY == true)) {
                                                        // Store this variable and value
                                                        pID = dictPipeMap1.Item[pRow];
                                                        // Pipeline number
                                                        yName = ("Y"
                                                                    + (fNode
                                                                    + (tNode
                                                                    + (pID + tName))));
                                                        // y-variable name
                                                        // Check if the vriable exists - if not, try the reverse name (for bi-directional arcs)
                                                        if ((dictVariables.Exists(yName) == false)) {
                                                            yName = ("Y"
                                                                        + (tNode
                                                                        + (fNode
                                                                        + (pID + tName))));
                                                            // y-variable name
                                                            if ((dictVariables.Exists(yName) == false)) {

                                                            }

                                                        }

                                                        dictVariables.Item[yName] = "1";
                                                        if ((dictCheckV.Exists(yName) == true)) {

                                                        }

                                                        dictCheckV.Add;
                                                        yName;
                                                        true;
                                                    }
                                                    else {
                                                        xName = ("X"
                                                                    + (fNode + (","
                                                                    + (tNode + tName))));
                                                        if ((dictVariables.Exists(xName) == false)) {

                                                        }

                                                        dictVariables.Item[xName] = xFlow.ToString();
                                                        if ((dictCheckV.Exists(xName) == true)) {

                                                        }

                                                        dictCheckV.Add;
                                                        xName;
                                                        true;
                                                    }

                                                }
                                                else {
                                                    // We're in the same arc, which has laready been recorded
                                                    // Set arc1 and arc2 to be equal
                                                    arc2 = arc1;
                                                }

                                            }

                                        }

                                        // Close the solution file we're reading FROM
                                        Close;
                                        // TODO: # ... Warning!!! not translated
                                        openSTART;
                                        // Loop through constraints
                                        lineCount = -1;
                                        // Loop through every variable
                                        for (entry : dictVariables) {
                                            lineCount = (lineCount + 1);
                                            vName = ('\"'
                                                        + (entry + '\"'));
                                            indexName = ('\"'
                                                        + (lineCount.ToString() + '\"'));
                                            valueString = ('\"'
                                                        + (dictVariables.Item[entry] + '\"'));
                                            // Write the solution file
                                            Print;
                                            // TODO: # ... Warning!!! not translated
                                            saveSOL;
                                            ("  <variable name="
                                                        + (vName + (" index="
                                                        + (indexName + (" value="
                                                        + (valueString + "/>"))))));
                                        }

                                        // Print end of file
                                        Print;
                                        // TODO: # ... Warning!!! not translated
                                        saveSOL;
                                        " </variables>";
                                        Print;
                                        // TODO: # ... Warning!!! not translated
                                        saveSOL;
                                        "</CPLEXSolution>";
                                        Close;
                                        // TODO: # ... Warning!!! not translated
                                        saveSOL;
                                        CreateStartingSolution = true;
                                        // TODO: Exit Function: Warning!!! Need to return the value
                                        return;
                                    err:
                                        // Get sampled number
                                        ((float)(GetSampleNumber(((string)(inFile)), ((int)(realization)), ((int)(column)))));
                                        // TODO: On Error GoTo Warning!!!: The statement is not translatable
                                        handle = FreeFile;
                                        Open;
                                        inFile;
                                        for (object Input; ; Input++) {
                                            // TODO: # ... Warning!!! not translated
                                            handle;
                                            // Read in header
                                            for (n = 1; (n <= 4); n++) {
                                                Line;
                                                Input;
                                                // TODO: # ... Warning!!! not translated
                                                handle;
                                                readString;
                                            }

                                            // Read down to the correct line
                                            for (n = 1; (n <= realization); n++) {
                                                Line;
                                                Input;
                                                // TODO: # ... Warning!!! not translated
                                                handle;
                                                readString;
                                            }

                                            // Close the file
                                            Close;
                                            // TODO: # ... Warning!!! not translated
                                            handle;
                                            // Get data
                                            stringArray = readString.Split('\t');
                                            GetSampleNumber = float.Parse(stringArray[column]);
                                            // TODO: Exit Function: Warning!!! Need to return the value
                                            return;
                                        err:
                                            MsgBox;
                                            "Error in GetSampleNumber";
                                            vbCritical;
                                            "Error";
                                            // Return a normally-distributed random number
                                            ((float)(NormalDistribution(((float)(mean)), ((float)(stdDeviation)))));
                                            double fac;
                                            double r;
                                            double V1;
                                            double V2;
                                            double gauss;
                                        Line1:
                                            V1 = ((2 * Rnd)
                                                        - 1);
                                            V2 = ((2 * Rnd)
                                                        - 1);
                                            r = (V1
                                                        | ((2 + V2)
                                                        | 2));
                                            // TODO: Warning!!! The operator should be an XOR ^ instead of an OR, but not available in CodeDOM
                                            // TODO: Warning!!! The operator should be an XOR ^ instead of an OR, but not available in CodeDOM
                                            if ((r >= 1)) {
                                                goto Line1;
                                            }

                                            fac = Sqr(((2
                                                            * (Log(r) / r))
                                                            * -1));
                                            gauss = (V2 * fac);
                                            if (((gauss * stdDeviation)
                                                        + (mean < 0))) {
                                                NormalDistribution = 0;
                                            }
                                            else {
                                                NormalDistribution = ((gauss * stdDeviation)
                                                            + mean);
                                            }

                                            // Return a number as string
                                            ((string)(NumberAsString(((void)(inNumber)), Variant)));
                                            if ((decimalMultiplier == 0)) {
                                                // NumberAsString = CStr(CLng(inNumber))
                                                NumberAsString = Int((inNumber + 0.5)).ToString();
                                            }
                                            else {
                                                inNumber = (float.Parse(Int(((inNumber * decimalMultiplier)
                                                                    + 0.5))) / decimalMultiplier);
                                                NumberAsString = inNumber.ToString();
                                            }

                                            // Return a number as CO2 string
                                            ((string)(CO2AsString(((void)(inNumber)), Variant)));
                                            inNumber = (inNumber / co2Divider);
                                            // Convert number to 1/1000
                                            inNumber = (float.Parse(Int(((inNumber * co2Divider)
                                                                + 0.5))) / co2Divider);
                                            // Convert to 3 decimal places
                                            CO2AsString = inNumber.ToString();
                                            // Return a number as COST string
                                            ((string)(CostAsString(((void)(inNumber)), Variant)));
                                            inNumber = (inNumber / costDivider);
                                            // Convert number to 1/1000
                                            inNumber = (float.Parse(Int(((inNumber * costDivider)
                                                                + 0.5))) / costDivider);
                                            // Convert to 3 decimal places
                                            CostAsString = inNumber.ToString();
                                            // Function returns the exact variable name of a vriable with a non-zero co-efficient
                                            // If one does not exist, it just uses the first case of that variable
                                            ((string)(GetVariableToWrite(((Dictionary)(varDict)), ((Dictionary)(costDict)))));
                                            long node1;
                                            string node2;
                                            for (entry : varDict) {
                                                node1 = varDict.Item[entry];
                                                // Get the node as cell number
                                                node2 = dictNodesMap1.Item[node1];
                                                // Node
                                                if ((costDict.Item[entry] > 0)) {
                                                    // Check if coefficient is non-zero
                                                    GetVariableToWrite = node2;
                                                    // Store variable NUMBER
                                                    // TODO: Exit Function: Warning!!! Need to return the value
                                                    return;
                                                    // Exit
                                                }

                                            }

                                            // All variables have zero coefficients - store first variable name
                                            for (entry : varDict) {
                                                node1 = varDict.Item[entry];
                                                // Get the node as cell number
                                                node2 = dictNodesMap1.Item[node1];
                                                // Node
                                                GetVariableToWrite = node2.ToString();
                                                // TODO: Exit Function: Warning!!! Need to return the value
                                                return;
                                            }

                                            ((string)(GetVariableToWriteYijd(((Dictionary)(varDict)))));
                                            string node1;
                                            string node2;
                                            for (entry : varDict) {
                                                stringArray = entry.Split('\t');
                                                // Entry
                                                node1 = dictNodesMap1.Item[long.Parse(stringArray[0])];
                                                // From node
                                                node2 = dictNodesMap1.Item[long.Parse(stringArray[1])];
                                                // To node node
                                                for (d = 1; (d <= UBound(pipeArray, 2)); d++) {
                                                    // Loop through pipe capacities
                                                    // Check if the pipeArray entry has a non-zero coefficient
                                                    if ((pipeArray(2, d) > 0)) {
                                                        // GetVariableToWriteYijd = CStr(node1) + "," + CStr(node2) + "," + CStr(pipeArray(1, d))
                                                        GetVariableToWriteYijd = (node1
                                                                    + (node2 + pipeID));
                                                        // TODO: Exit Function: Warning!!! Need to return the value
                                                        return;
                                                    }

                                                }

                                            }

                                            // No non-zero coefficients exist - just pick first entry
                                            for (entry : varDict) {
                                                stringArray = entry.Split('\t');
                                                // Entry
                                                node1 = dictNodesMap1.Item[long.Parse(stringArray[0])];
                                                // From node
                                                node2 = dictNodesMap1.Item[long.Parse(stringArray[1])];
                                                // To node node
                                                for (d = 1; (d <= UBound(pipeArray, 2)); d++) {
                                                    // Loop through pipe capacities
                                                    GetVariableToWriteYijd = (node1.ToString()
                                                                + (node2.ToString() + pipeArray(1, d).ToString()));
                                                    // TODO: Exit Function: Warning!!! Need to return the value
                                                    return;
                                                }

                                            }

                                            ((string)(GetNumberAsText(((void)(inNumber)), Variant)));
                                            GetNumberAsText = inNumber.ToString();
                                            switch (GetNumberAsText.Length) {
                                                case 1:
                                                    GetNumberAsText = ("000" + GetNumberAsText);
                                                    break;
                                                case 2:
                                                    GetNumberAsText = ("00" + GetNumberAsText);
                                                    break;
                                                case 3:
                                                    GetNumberAsText = ("0" + GetNumberAsText);
                                                    break;
                                                case 4:
                                                    // Nothing
                                                    break;
                                                default:
                                                    MsgBox;
                                                    "Too many numbers to represent Yijd variable in MPS format";
                                                    vbOKOnly;
                                                    "Error";
                                                    break;
                                            }
                                        }

                                    }

                                }

                            }

                        }

                    }

                }

            }

        }

    }
}